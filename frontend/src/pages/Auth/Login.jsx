import React, { useState } from "react";
import AuthLayout from "../../components/layout/AuthLayout";
import { Link, useNavigate } from "react-router-dom";
import Input from "../../components/inputs/Input";
import axios from "axios";
import { jwtDecode } from "jwt-decode"; // fixed import syntax
import "../../index.css";

const Login = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleFormChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!validateEmail(formData.email)) {
      setError("Please enter a valid email address");
      return;
    }

    if (!formData.password || formData.password.length < 8) {
      setError("Password must be at least 8 characters long");
      return;
    }

    setError("");

    try {
      const response = await axios.post(
        "http://localhost:8081/auth/signin",
        formData
      );
      localStorage.setItem("jwt", response.data.jwt);

      const decoded = jwtDecode(response.data.jwt);
      console.log("Decoded JWT:", decoded);
      const roles = decoded.authorities
        ? Array.isArray(decoded.authorities)
          ? decoded.authorities
          : [decoded.authorities]
        : [];

      localStorage.setItem("roles", JSON.stringify(roles));

      // Fix role check logic and navigate accordingly
      if (roles.includes("ROLE_ADMIN") || roles.includes("ROLE_MANAGER")) {
        navigate("/admin/dashboard");
      } else {
        navigate("/user/dashboard");
      }
    } catch (err) {
      console.error(err);
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Login failed. Please check your credentials.");
      }
    }
  };

  return (
    <AuthLayout>
      <div className="lg:w-[70%] h-3/4 md:h-full flex flex-col justify-center">
        <h3 className="text-xl font-semibold text-black">Welcome Back</h3>
        <p className="text-xs text-slate-700 mt-[5px] mb-6">
          Please enter your details to log in
        </p>

        <form onSubmit={handleLogin}>
          <Input
            value={formData.email}
            onChange={(e) => handleFormChange("email", e.target.value)}
            label="Email"
            placeholder="john@example.com"
            type="text"
          />

          <Input
            value={formData.password}
            onChange={(e) => handleFormChange("password", e.target.value)}
            label="Password"
            placeholder="Min 8 Characters"
            type="password"
          />

          {error && <p className="text-red-500 text-xs pb-2.5">{error}</p>}
          <button type="submit" className="btn-primary">
            LOGIN
          </button>

          <p className="text-[15px] text-slate-800 mt-3">
            Don't have an account?{" "}
            <Link className="font-medium text-blue-600 underline" to="/signup">
              SignUp
            </Link>
          </p>
        </form>
      </div>
    </AuthLayout>
  );
};

export default Login;
