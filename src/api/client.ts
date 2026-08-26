import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // Must be '/api' so Vite proxy catches it
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;