export default function InputField({ label, placeholder, inputType, value, setValue, step ,error}) {
  const handleChange = (e) => {
    if (inputType === "number") {
      setValue(e.target.value === "" ? "" : parseFloat(e.target.value));
    } else {
      setValue(e.target.value);
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-1">
      <label className="block text-base font-semibold ">{label}</label>
      {
        error ? <i className="text-red-500">*{error}</i> : ""
      }
      
      </div>
      
      <input
        type={inputType}
        value={value}
        placeholder={placeholder}
        step={step}           // allows decimals
        className="border py-1.5 px-3 rounded w-full bg-input-bg focus:outline-none focus:ring-0 focus:border-grey-600 mb-2"
        min={0}
        onChange={handleChange}
      />
    </div>
  );
}