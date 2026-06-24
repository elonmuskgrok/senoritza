import React from "react";
import { Link, useNavigate } from "react-router-dom";

export const Navbar = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user") || "{}");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark shadow-sm py-3" style={{ backgroundColor: "#4a154b" }}>
      <div className="container">
        <Link to={token ? "/dashboard" : "/"} className="navbar-brand fw-bold fs-3 d-flex align-items-center">
          <i className="bi bi-briefcase-fill me-2"></i> TaxTracker
        </Link>
        <div className="d-flex ms-auto gap-3 align-items-center">
          {token ? (
            <>
              <span className="text-white fw-semibold me-3 d-none d-md-inline">
                <i className="bi bi-person-circle me-1"></i> {user.name}
              </span>
              <Link to="/dashboard" className="btn btn-outline-light px-3 fw-semibold d-none d-md-inline-block">
                Dashboard
              </Link>
              <button onClick={handleLogout} className="btn btn-light text-dark px-4 fw-semibold shadow-sm">
                <i className="bi bi-box-arrow-right me-2"></i>Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-outline-light px-4 fw-semibold">
                Login
              </Link>
              <Link to="/register" className="btn btn-light text-dark px-4 fw-semibold shadow-sm">
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};
