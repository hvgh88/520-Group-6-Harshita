import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Registration.css';

const Login = () => {
  const [userId, setUserId] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`http://localhost:8080/user/${userId}`);
      if (response.ok) {
        const userData = await response.json();
        localStorage.setItem('userId', userData.id);
        localStorage.setItem('username', userData.username);
        navigate('/groups');
      } else {
        alert('User not found');
      }
    } catch (error) {
      console.error('Error logging in:', error);
    }
  };

  return (
    <div className="registration-container">
      <div className="registration-card">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <input
              type="number"
              placeholder="Enter your user ID"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="register-btn">
            Login
          </button>
        </form>
        <p className="switch-auth">
          New user? <Link to="/register">Register here</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;