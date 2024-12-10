import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { HiHome, HiUserGroup, HiChat, HiUser, HiLogout } from "react-icons/hi";
import "./TopNav.css";
import logo from "../../assets/logo.png";

const TopNav = () => {
  const navigate = useNavigate();
  const menuItems = [
    { path: "/", icon: HiHome, label: "Home" },
    { path: "/groups", icon: HiUserGroup, label: "Groups" },
    { path: "/chat", icon: HiChat, label: "Chat" },
  ];

  const handleLogout = () => {
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <nav className="top-nav">
      <div className="nav-content">
        <div className="nav-left">
          <img src={logo} alt="Logo" height="32" />
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => 
                `nav-link ${isActive ? 'active' : ''}`
              }
            >
              <item.icon className="icon" />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </div>
        <div className="nav-right">
          <button onClick={handleLogout} className="logout-btn">
            <HiLogout className="icon" />
            <span>Logout</span>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default TopNav;