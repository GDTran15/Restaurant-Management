export default function InputField({ label, placeholder, inputType, value, setValue, step }) {
  const handleChange = (e) => {
    if (inputType === "number") {
      setValue(e.target.value === "" ? "" : parseFloat(e.target.value));
    } else {
      setValue(e.target.value);
    }
  };

  return (
    <div>
      <label className="block text-base font-semibold mb-2">{label}</label>
      <input
        type={inputType}
        value={value}
        placeholder={placeholder}
        step={step}           // allows decimals
        className="border p-1.5 rounded w-full bg-input-bg focus:outline-none focus:ring-0 focus:border-grey-600 mb-2"
        min={0}
        onChange={handleChange}
      />
    </div>
  );
}