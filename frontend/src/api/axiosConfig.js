import axios from 'axios';

const API_BASE_URL = "http://localhost:8080/banksimulation/api";

// Create a central, configured axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true // 👈 This is the most important part!
                        // It tells axios to send cookies with every request.
});

// We default export the instance, but keep the URL export just in case
export { API_BASE_URL };
export default api;