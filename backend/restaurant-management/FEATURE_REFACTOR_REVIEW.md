Feature refactor and backend review — 4 October 2026

Moved 140 Java source/test files into feature packages. Updated package declarations, imports, cross-feature model references, and fully qualified DTO/enum references inside JPQL queries. Existing business logic, endpoint paths, database mappings, dependency configuration, and uncommitted edits were preserved. The findings below are recommendations, not applied fixes.

**Folder structure**

Under `src/main/java/com/duong/RestaurantManagement/`:

```text
RestaurantManagementApplication.java
auth/       authentication, JWT, security configuration, login/register DTOs
user/       users, roles, permissions
order/      orders and order items
food/       foods, categories and category mappings
menu/       menus and menu items
table/      restaurant tables and QR codes
dining/     dining sessions
invoice/    invoices
payment/    payments and PayPal configuration/services
member/     members, memberships and ranks
common/exception/  shared errors and the global exception handler
```

Each feature contains only the layers it needs. For example:

```text
order/
  controller/OrderController.java
  service/OrderService.java
  service/OrderServiceImp.java
  service/OrderItemService.java
  service/OrderItemServiceImp.java
  repository/OrderRepo.java
  repository/OrderItemRepo.java
  model/Order.java
  model/OrderItem.java
  model/OrderStatus.java
  dto/request/...
  dto/response/...
  mapper/OrderMapper.java
  exception/InvalidOrderStateException.java
```

The existing order unit test now lives in `src/test/java/com/duong/RestaurantManagement/order/service/`. Service interfaces and implementations share each feature's `service` package. Existing class names were retained. The application class remains at the package root for component/entity/repository scanning.

**Verification and limits**

- Before the move, `mvn -Dtest=OrderServiceImpTest test` compiled but failed with `UnsupportedClassVersionError`: Java 21 preview features were not enabled for the test JVM.
- After the move, `mvn clean test -Dtest=OrderServiceImpTest -DargLine=--enable-preview` passed: 140 main sources and two test sources compiled; one test ran, with zero failures/errors.
- Compared all 142 Java files against a snapshot of the working tree taken before the move. After accounting for packages/imports and relocated fully qualified names, bodies were unchanged.
- No references to the old layer packages remain under `src`.
- The database-backed context test, HTTP flows, PayPal sandbox transactions, and concurrency scenarios were not run. The configured context test uses the application's PostgreSQL configuration with `ddl-auto=update`; a separate test database/profile is needed for isolated integration checks. Compilation does not validate every JPQL query at runtime.

Source links below are relative to this project. Findings are based on the current working tree, including pre-existing uncommitted changes. Runtime consequences described below are code-review findings unless explicitly identified as tested.

**Highest priority**

1. **Sensitive operations are public.** [SecurityConfiguration.java](src/main/java/com/duong/RestaurantManagement/auth/config/SecurityConfiguration.java), `securityFilterChain`: the first matcher permits every method under `/orders/**`, `/payments/**`, `/invoices/**`, `/members/**`, `/memberships/**`, and `/menus/**`. Later order/menu restrictions cannot override that earlier match. An unauthenticated request can reach cash settlement, order cancellation, membership creation and menu modification. Split customer read/order routes from staff mutations, enforce permissions per operation, and verify a customer's table/session access instead of trusting a supplied numeric session ID.

2. **Login and role checks are broken independently.** [UserPrincipal.java](src/main/java/com/duong/RestaurantManagement/auth/model/UserPrincipal.java) returns empty username/password strings and an empty authority list instead of the stored user's values. BCrypt authentication cannot check the stored password through this principal. Separately, [Role.java](src/main/java/com/duong/RestaurantManagement/user/model/Role.java), `getAuthorities`, generates `ROLE_canADMIN`/`ROLE_canMANAGER`, while `hasAnyRole(ADMIN.name())` expects `ROLE_ADMIN`. Delegate principal values to the user and correct the role prefix; both problems need attention.

3. **Credentials are stored in a tracked properties file.** [application.properties](src/main/resources/application.properties) contains literal database, JWT and PayPal credential values. Their validity was not tested and values are intentionally omitted here. Move secrets to environment-backed configuration; rotate any real values that have been shared or committed, and assess repository history exposure.

4. **Order quantities are not validated.** [OrderController.java](src/main/java/com/duong/RestaurantManagement/order/controller/OrderController.java), [AddOrderDTO.java](src/main/java/com/duong/RestaurantManagement/order/dto/request/AddOrderDTO.java), and [OrderItemDTO.java](src/main/java/com/duong/RestaurantManagement/order/dto/request/OrderItemDTO.java) lack request validation. [FoodRepo.java](src/main/java/com/duong/RestaurantManagement/food/repository/FoodRepo.java), `decreaseFoodQuantity`, subtracts the supplied quantity: a negative value increases stock and produces a negative order total. Zero quantities and empty orders are also accepted. Validate a non-null session, a non-empty item list, nested items, positive IDs and positive quantities before writing data.

5. **PayPal capture occurs before checking local payment/invoice state.** [PaymentServiceImp.java](src/main/java/com/duong/RestaurantManagement/payment/service/PaymentServiceImp.java), `captureOrder`: the external capture runs before the payment lookup and before `markInvoiceAsPaid` checks the invoice. Example: create a PayPal order, settle the invoice in cash, then capture the old PayPal order. PayPal can charge successfully before the local code rejects the already-paid invoice. A database rollback cannot undo that charge. Validate and reserve local state before capture, make retries idempotent, and reconcile external success when local persistence fails.

6. **An invoice can miss later orders.** [InvoiceServiceImp.java](src/main/java/com/duong/RestaurantManagement/invoice/service/InvoiceServiceImp.java), `createNewInvoice`, snapshots totals but leaves the dining session active. [OrderServiceImp.java](src/main/java/com/duong/RestaurantManagement/order/service/OrderServiceImp.java), `addOrder`, only checks that the session is active, so further orders are accepted. Returning an existing unpaid invoice does not recalculate its total, and payment closes the session without checking for those new orders. Freeze ordering at checkout or explicitly invalidate/recalculate the invoice, and revalidate before settlement.

7. **Payment currency is client-controlled and the captured amount is not checked against the current invoice.** [PaymentServiceImp.java](src/main/java/com/duong/RestaurantManagement/payment/service/PaymentServiceImp.java), `createOrder`, combines the server's numeric invoice total with `request.currencyCode()`. The payment entity does not persist a currency, and capture does not reconcile the captured amount/currency with the invoice. A member discount can also change the invoice after PayPal order creation. Define the restaurant currency server-side and bind the payment attempt to a fixed invoice amount/currency.

**Other correctness issues**

8. **JWT errors do not reliably produce a 401.** [JwtService.java](src/main/java/com/duong/RestaurantManagement/auth/service/JwtService.java), `extractAllClaims`, wraps `JwtException` in plain `RuntimeException`. [JwtFilter.java](src/main/java/com/duong/RestaurantManagement/auth/config/JwtFilter.java) catches JWT exceptions, so the wrapped error escapes; its existing catch blocks also neither return an error response nor continue the filter chain. Preserve meaningful exception types and explicitly handle authentication failures in the filter.

9. **Refresh handling has a null-cookie failure and no token-purpose check.** [AuthenticationServiceImpl.java](src/main/java/com/duong/RestaurantManagement/auth/service/AuthenticationServiceImpl.java), `refreshToken`, iterates `getCookies()` without checking for null. A request without cookies can therefore fail instead of returning 401. [JwtService.java](src/main/java/com/duong/RestaurantManagement/auth/service/JwtService.java) signs access and refresh tokens the same way and validates only subject/expiry; it does not enforce which kind is accepted by each endpoint. After the principal issue is fixed, a refresh token can satisfy the bearer-token validator. Check token purpose explicitly and handle missing/invalid refresh tokens consistently.

10. **Several state transitions can race.** [OrderServiceImp.java](src/main/java/com/duong/RestaurantManagement/order/service/OrderServiceImp.java), `cancelOrder`, checks state and restores stock without locking/versioning: concurrent cancellations can both restore inventory. [PaymentServiceImp.java](src/main/java/com/duong/RestaurantManagement/payment/service/PaymentServiceImp.java), `createCashPayment`, can likewise record two payments after both requests read UNPAID. [DiningSessionServiceImp.java](src/main/java/com/duong/RestaurantManagement/dining/service/DiningSessionServiceImp.java), `getDiningSession`, can create two active sessions for the same table; creation and the table update are not in one service transaction. Use atomic state transitions or appropriate locking/versioning, plus database uniqueness rules for business invariants. A transaction alone does not make the state checks exclusive.

11. **Unavailable/off-menu food can be ordered.** [OrderItemServiceImp.java](src/main/java/com/duong/RestaurantManagement/order/service/OrderItemServiceImp.java) checks food existence and stock, but not `isAvailable` or membership in the active menu. Validate those rules server-side; hiding items in the frontend is insufficient.

12. **Editing food inserts another category mapping.** [FoodServiceImpl.java](src/main/java/com/duong/RestaurantManagement/food/service/FoodServiceImpl.java), `updateFoodInformation`, calls [FoodCategoryMapService.java](src/main/java/com/duong/RestaurantManagement/food/service/FoodCategoryMapService.java), `mapFoodToCategory`, which always inserts. Repeated edits retain the old category and can create duplicate mappings, affecting joined food lists and category counts. Replace/update the existing mapping if a food has one category; otherwise reconcile the requested category set and prevent duplicate pairs.

13. **The menu toggle bypasses the guarded activation paths.** [MenuController.java](src/main/java/com/duong/RestaurantManagement/menu/controller/MenuController.java), `toggleMenu`, calls [MenuServiceImp.java](src/main/java/com/duong/RestaurantManagement/menu/service/MenuServiceImp.java), `changeMenuActivation`, which skips the checks in `activateMenu`/`deactivateMenu`. This permits multiple active menus or deactivation during dining. Route every activation change through the same checks and enforce the single-active-menu invariant under concurrent requests.

14. **Money and historical prices need consistent storage.** [Invoice.java](src/main/java/com/duong/RestaurantManagement/invoice/model/Invoice.java), order totals, food prices and member spend use `double`, while payments use `BigDecimal`. Rounding only when creating payment amounts leaves earlier calculations exposed to floating-point differences. Use decimal amounts or integer minor units consistently. Also, [DiningSessionServiceImp.java](src/main/java/com/duong/RestaurantManagement/dining/service/DiningSessionServiceImp.java), `getDiningSessionOrderItems`, displays the food's current price against previously recorded item totals. Store and display the order-time unit price, and avoid merging historical rows with different unit prices as if they shared one price.

15. **Membership configuration is insufficiently validated.** [AddMembershipRequest.java](src/main/java/com/duong/RestaurantManagement/member/dto/request/AddMembershipRequest.java) has no constraints on rank, minimum spend or discount rate, and [Membership.java](src/main/java/com/duong/RestaurantManagement/member/model/Membership.java) has no unique rank constraint. A discount above 1 can produce negative invoice totals; duplicate rank rows can break the single-result discount lookup. Validate ranges and enforce rank uniqueness.

**Build, deployment and maintenance**

16. **The default unit-test command currently fails.** This was reproduced before refactoring. [OrderServiceImp.java](src/main/java/com/duong/RestaurantManagement/order/service/OrderServiceImp.java) uses Java 21 preview string templates, but [pom.xml](pom.xml) enables preview only for compilation. Either replace those templates with stable Java syntax or consistently configure preview for tests and application startup. The successful verification used a command-line flag; the POM was not fixed.

17. **Dependency versions are inconsistent.** [pom.xml](pom.xml) uses Spring Boot 4.0.3, springdoc 2.3.0 and an explicitly pinned WebFlux starter 3.5.13. The official [springdoc compatibility matrix](https://springdoc.org/faq.html) pairs Boot 4.0.x with springdoc 3.0.x. Align springdoc with Boot and let Boot manage its own starter versions. This is a compatibility finding; a startup failure was not reproduced in this review.

18. **Development settings need an explicit deployment profile.** [RestaurantTableServiceImpl.java](src/main/java/com/duong/RestaurantManagement/table/service/RestaurantTableServiceImpl.java) generates QR links pointing at localhost, so a customer's phone will not reach the restaurant server. CORS also hard-codes localhost. [PaypalConfiguration.java](src/main/java/com/duong/RestaurantManagement/payment/config/PaypalConfiguration.java) always uses SANDBOX even though a mode property is injected. The refresh cookie is marked `Secure=false`, expires after one day while the JWT allows seven days, and `jwt.expiration=36000` means 36 seconds in the current millisecond-based implementation. Confirm intended lifetimes and move environment-specific URLs, security flags and provider mode into deployment configuration. Use schema migrations instead of `ddl-auto=update` for controlled deployments.

19. **Tests cover very little of the business behavior.** The only business unit test is one successful order creation; the other test loads the application context. Prioritize tests for rejected quantities, role protection, invalid JWTs, repeated cancellation, stale invoices, duplicate payment/capture recovery and simultaneous table scans. Isolate integration tests from the working restaurant database.

20. **There is removable duplication, but it was left intact.** [UserServiceImp.java](src/main/java/com/duong/RestaurantManagement/user/service/UserServiceImp.java) duplicates registration in `AuthenticationServiceImpl`; `UserController` is empty and `UserAccessController` is entirely commented out. `PaypalAuthenticationService` has no caller in the reviewed backend, alongside an already-configured PayPal SDK. The `untitled/` directory contains a separate Maven archetype scaffold. Confirm whether these are still needed before removing them. Standardizing `Imp`/`Impl` naming and removing same-package/unused imports can be a later cleanup; neither is required for this folder move.

Suggested order: secure routes and repair login first; fix order/payment invariants next; then address concurrency, dependency/runtime configuration, and cleanup. No bug fixes from this list were applied.
