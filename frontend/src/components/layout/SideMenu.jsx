import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { SIDE_MENU_DATA, SIDE_MENU_USER_DATA } from "../../utils/data";
import axios from "axios";
import {jwtDecode} from "jwt-decode";
import AddProjectForm from "../AddProjectForm";

const SideMenu = ({ activeMenu }) => {
  const [userData, setUserData] = useState(null);
  const [sideMenuData, setSideMenuData] = useState([]);
  const [role, setRole] = useState("");

  const navigate = useNavigate();

  const handleClick = (route) => {
    if (route === "logout") {
      handleLogOut();
      return;
    }
    navigate(route);
  };

  const handleLogOut = () => {
    localStorage.clear();
    setUserData(null);
    navigate("/login");
  };

  useEffect(() => {
    const fetchUserProfile = async () => {
      const token = localStorage.getItem("jwt");

      if (!token) {
        console.error("No token found");
        return;
      }

      try {
        const decoded = jwtDecode(token);
        setRole(decoded.authorities || "");

        const api = axios.create({
          baseURL: "http://localhost:8081",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        const response = await api.get("/api/user/profile");
        console.log("User profile fetched successfully:", response.data);
        setUserData(response.data);
      } catch (error) {
        console.error("Failed to fetch user profile", error);
      }
    };

    fetchUserProfile();
  }, []);

  useEffect(() => {
    if (userData?.role) {
      if (userData.role === "MANAGER" || userData.role === "ADMIN") {
        setSideMenuData(SIDE_MENU_DATA);
        console.log("admin or manager data fetched");
      } else {
        setSideMenuData(SIDE_MENU_USER_DATA);
         console.log("user data fetched");
      }
    }
  }, [userData]);

  return (
    <div className="w-64 h-[calc(100vh-61px)] bg-white border-r border-gray-200/50 sticky top-[61px] z-20">
      {console.log("User data",userData)}
      <div className="flex flex-col items-center justify-center mb-7 pt-5">
        <div className="relative">
          <img
            src={`${userData?.avatarUrl}` || ""}
            // src="https://res.cloudinary.com/dfsoff19s/image/upload/v1754658162/document/qi23tdztnaarorptsmmq.png"
            alt="profile"
            className="w-20 h-20 bg-slate-400 rounded-full"
          />
        </div>
        {userData?.role === "ADMIN" && (
          <div className="text-[10px] font-medium text-white bg-blue-600 px-3 py-0.5 rounded mt-1">
            Admin
          </div>
        )}
        <h5 className="text-gray-950 font-medium leading-6 mt-3">
          {userData?.name || ""}
        </h5>
        <p className="text-[12px] text-gray-500">{userData?.email || ""}</p>
      </div>
      {sideMenuData.map((item, index) => (
        <button
          key={`menu_${index}`}
          className={`w-full flex items-center gap-4 text-[15px] ${
            activeMenu === item.label
              ? "text-blue-600 bg-linear-to-r from-blue-50/40 to-blue-100/50 border-r-3"
              : ""
          } py-3 px-6 mb-3 cursor-pointer`}
          onClick={() => handleClick(item.path)}
        >
          <item.icon className="text-xl" />
          {item.label}
        </button>
      ))}
    </div>
  );
};

export default SideMenu;
