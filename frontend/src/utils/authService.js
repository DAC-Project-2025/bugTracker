import api from "./api";

export const signup = async (userData) => {
  const res = await api.post("/auth/signup", userData);
  return res.data;
};

export const signin = async (credentials) => {
  const res = await api.post("/auth/signin", credentials);
  localStorage.setItem("token", res.data.token); // store JWT for later
  return res.data;
};