import axios from "axios";

const baseURL = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

const api = axios.create({
  baseURL,
  withCredentials: true,
});

export const publicApi = axios.create({
  baseURL,
  withCredentials: false,
});

export default api;
