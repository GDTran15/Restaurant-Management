/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
       colors: {
        primary: '#1d4ed8', 
        'input-bg': '#F9F9F2',
        'main-navy': '#001F3F',
      },
    },
  },
  plugins: [],
}

