import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // Relative path routes through Vite's proxy on localhost:5173
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;