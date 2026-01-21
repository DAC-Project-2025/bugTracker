
import React from "react";
import {jwtDecode} from "jwt-decode";
import { Navigate, Outlet } from "react-router-dom";

const PrivateRoutes = ({ allowedRoles }) => {
  // Example: get user roles from localStorage or context
  // Your JWT roles might look like: ["ROLE_ADMIN", "ROLE_USER"]

  const token = localStorage.getItem("jwt");
  let storedRoles = [];
try {
  const rolesStr = localStorage.getItem("roles");
  if (rolesStr && rolesStr !== "undefined") {
    storedRoles = JSON.parse(rolesStr);
  }
} catch (error) {
  storedRoles = [];
}

  // Normalize roles by removing 'ROLE_' prefix and uppercase
  const userRoles = storedRoles.map(role =>
    role.toUpperCase().replace("ROLE_", "")
  );

  // Check if any role of user matches allowedRoles
  const hasAccess = allowedRoles.some(role =>
    userRoles.includes(role.toUpperCase())
  );

   if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (!hasAccess) {
    // Redirect to login or unauthorized page
    return <Navigate to="/login" replace />;
  }

  // Render child routes (nested routes)
  return <Outlet />;
};

export default PrivateRoutes;
