import axios from 'axios';

const API_BASE_URL = "http://localhost:8080/banksimulation/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true
});

export { API_BASE_URL };
export default api;