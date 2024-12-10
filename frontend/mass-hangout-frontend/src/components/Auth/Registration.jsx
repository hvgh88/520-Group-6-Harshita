import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Registration.css';

const Registration = () => {
  const [username, setUsername] = useState('');
  const [userId, setUserId] = useState(null);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/user/create/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username }),
      });
      
      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('userId', data.id);
        localStorage.setItem('username', username);
        navigate('/groups');
      }
    } catch (error) {
      console.error('Error creating user:', error);
    }
  };

  return (
    <div className="registration-container">
      <div className="registration-card">
        <h2>Welcome</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <input
              type="text"
              placeholder="Enter username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="register-btn">
            Get Started
          </button>
          <p className="switch-auth">Already have an ID? <Link to="/login">Login here</Link></p>
        </form>
      </div>
    </div>
  );
};

export default Registration;