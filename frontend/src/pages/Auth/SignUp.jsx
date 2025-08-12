import React, { useState } from "react";
import ProfilePhotoSelector from "../../components/inputs/ProfilePhotoSelector";
import AuthLayout from "../../components/layout/AuthLayout";
import Input from "../../components/inputs/Input";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const SignUp = () => {
  const [profilePic, setProfilePic] = useState(null);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    role: "USER",
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleFormChange = (field, value) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSignUp = async (e) => {
    e.preventDefault();

    if (!formData.name) return setError("Please enter full name");
    if (!formData.email) return setError("Please enter email");
    if (!formData.password || formData.password.length < 8)
      return setError("Password must be at least 8 characters long");

    setError("");
    setLoading(true);

    try {
      // let avatarUrl = ""; // If you want to support avatar upload, handle it here

      const avatarUrl = profilePic || "https://res.cloudinary.com/dfsoff19s/image/upload/v1754658162/document/qi23tdztnaarorptsmmq.png";
      const userData = { ...formData, avatarUrl };

      console.log("registration data", userData);
      const response = await axios.post(
        "http://localhost:8081/auth/signup",
        userData
      );
      localStorage.setItem("jwt", response.data.jwt);
      console.log("register success ", response.data);

      alert("Signup successful! Please login.");
      navigate("/login");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout>
      <div className="lg:w-[100%] h-auto md:h-full mt-10 md:mt-0 flex flex-col justify-center">
        <h3 className="text-xl font-semibold text-black">Create an Account</h3>
        <p className="text-xs text-slate-700 mt-[5px] mb-6">
          Join us today by entering your details below
        </p>

        <form onSubmit={handleSignUp}>
          {/* Profile Photo */}
          <ProfilePhotoSelector image={profilePic} setImage={setProfilePic} />

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              value={formData.name}
              onChange={(e) => handleFormChange("name", e.target.value)}
              label="Full Name"
              placeholder="John Doe"
            />
            <Input
              value={formData.email}
              onChange={(e) => handleFormChange("email", e.target.value)}
              label="Email"
              placeholder="john@example.com"
            />
            <Input
              value={formData.password}
              onChange={(e) => handleFormChange("password", e.target.value)}
              label="Password"
              placeholder="Min 8 Characters"
              type="password"
            />

            {/* Role Selection */}
            <div>
              <label className="text-[17px] text-slate-800">Role</label>
              <div className="input-box">
                <select
                  value={formData.role}
                  onChange={(e) => handleFormChange("role", e.target.value)}
                  className="w-full appearance-none bg-transparent focus:outline-none"
                >
                  <option value="USER">User</option>
                  <option value="MANAGER">Manager</option>
                </select>
              </div>
            </div>

            {error && (
              <p className="text-red-500 text-xs col-span-2">{error}</p>
            )}

            <button
              type="submit"
              className="btn-primary col-span-2"
              disabled={loading}
            >
              {loading ? "Signing Up..." : "SIGNUP"}
            </button>

            <p className="text-[15px] text-slate-800 mt-3 col-span-2">
              Already have an account?{" "}
              <Link className="font-medium text-blue-600 underline" to="/login">
                LogIn
              </Link>
            </p>
          </div>
        </form>
      </div>
    </AuthLayout>
  );
};

export default SignUp;
