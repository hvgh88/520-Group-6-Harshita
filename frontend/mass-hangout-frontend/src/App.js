import React from "react";
import { BrowserRouter as Router, Route, Routes, Navigate, useLocation } from "react-router-dom";
import TopNav from "./components/Navigation/TopNav"; 
import ChatPage from "./Pages/Chat/ChatPage";
import Groups from "./Pages/Groups/Groups";
import Login from "./components/Auth/Login";
import Registration from "./components/Auth/Registration";
import "./App.css";

const Dashboard = () => {
  return (
    <div style={{ padding: "40px" }}>
      <h1>UMass Hangout</h1>
      <h3>Plan your events in style!</h3>
    </div>
  );
};

const App = () => {
  const location = useLocation();
  const hideTopNav = location.pathname === "/login" || location.pathname === "/register";

  // Check if user is authenticated
  const isAuthenticated = !!localStorage.getItem('userId');

  return (
    <div className="app">
      {!hideTopNav && <TopNav />}
      <div className={`main-content ${hideTopNav ? "full-height" : ""}`}>
        <Routes>
          <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" />} />
          <Route path="/register" element={!isAuthenticated ? <Registration /> : <Navigate to="/dashboard" />} />
          
          {/* Protected Routes */}
          <Route path="/" element={isAuthenticated ? <Navigate to="/dashboard" /> : <Navigate to="/login" />} />
          <Route path="/dashboard" element={isAuthenticated ? <Dashboard /> : <Navigate to="/login" />} />
          <Route path="/chat" element={isAuthenticated ? <ChatPage /> : <Navigate to="/login" />} />
          <Route path="/groups" element={isAuthenticated ? <Groups /> : <Navigate to="/login" />} />
        </Routes>
      </div>
    </div>
  );
};

const WrappedApp = () => (
  <Router>
    <App />
  </Router>
);

export default WrappedApp;