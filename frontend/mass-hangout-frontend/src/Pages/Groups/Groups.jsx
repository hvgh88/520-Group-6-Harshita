import React, { useState, useEffect } from "react";
import { Users, Search, X, Clock, MapPin, Download } from "lucide-react";
import "./Groups.css";
import {useNavigate} from "react-router-dom";

const Groups = () => {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [groups, setGroups] = useState([]);
  const [joinedGroupIds, setJoinedGroupIds] = useState(new Set());
  const [feedback, setFeedback] = useState({ message: '', isError: false });
  const userId = localStorage.getItem('userId');
  const navigate = useNavigate();

  const [newGroup, setNewGroup] = useState({
    name: "",
    location: "",
    dateTime: "",
  });

  useEffect(() => {
    if (!userId) {
      navigate('/login');
      return;
    }
    fetchAllGroups();
    fetchUserGroups();
  }, []);

  const fetchAllGroups = async () => {
    try {
      const response = await fetch('http://localhost:8080/group/all');
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setGroups(data);
    } catch (error) {
      console.error("Error fetching groups:", error);
    }
  };

  const fetchUserGroups = async () => {
    try {
      const response = await fetch(`http://localhost:8080/user/group-ids/${userId}`);
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setJoinedGroupIds(new Set(data));
    } catch (error) {
      console.error("Error fetching user's groups:", error);
    }
  };

  const searchGroups = async () => {
    if (!searchQuery.trim()) {
      fetchAllGroups();
      return;
    }
    try {
      const response = await fetch('http://localhost:9200/groups/_search/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query: {
            multi_match: {
              query: searchQuery,
              fields: ["name", "location"]
            }
          }
        })
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      const searchResults = data.hits.hits.map(hit => ({
        id: hit._source.id,
        name: hit._source.name,
        location: hit._source.location,
        dateTime: new Date(hit._source.dateTime).toISOString()
      }));
      setGroups(searchResults);
    } catch (error) {
      console.error("Error searching groups:", error);
    }
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    searchGroups();
  };

  const handleCreateGroup = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`http://localhost:8080/group/create?userId=${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newGroup),
      });

      const responseText = await response.text();

      if (response.ok) {
        const groupId = responseText.split(':')[1].trim();
        setFeedback({ message: `Group created successfully with ID: ${groupId}`, isError: false });
        setGroups((prevGroups) => [...prevGroups, { ...newGroup, id: groupId }]);
        setShowCreateModal(false);
        setNewGroup({ name: "", location: "", dateTime: "" });
      } else {
        setFeedback({ message: responseText, isError: true });
      }
    } catch (error) {
      setFeedback({ message: "Error creating group: " + error.message, isError: true });
    }
  };

  const toggleJoin = async (groupId) => {
    if (!joinedGroupIds.has(groupId)) {
      try {
        const response = await fetch(`http://localhost:8080/group/join?groupId=${groupId}&userId=${userId}`, {
          method: 'POST',
        });
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        setJoinedGroupIds(prevIds => new Set(prevIds).add(groupId));
      } catch (error) {
        console.error(`Error joining group:`, error);
      }
    }
  };

  const handleDownloadInvite = async (groupId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/group/${groupId}/ics`);
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = `group_${groupId}_invite.ics`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Error downloading invite:", error);
    }
  };

  return (
    <div className="groups-container">
      <div className="groups-header">
        <h1>Groups</h1>
        <button className="create-group-btn" onClick={() => setShowCreateModal(true)}>
          Create Group
        </button>
      </div>

      <form className="groups-search" onSubmit={handleSearchSubmit}>
        <Search className="search-icon" />
        <input
          type="text"
          placeholder="Search groups..."
          className="search-input"
          value={searchQuery}
          onChange={handleSearchChange}
        />
      </form>

      <div className="groups-list">
        {groups.map((group) => (
          <div key={group.id} className="group-card">
            <div className="group-info">
              <div className="group-avatar">
                <Users className="group-avatar-icon" />
              </div>
              <div className="group-details">
                <h3>{group.name || "Unnamed Group"}</h3>
                <div className="group-meta">
                  <span className="group-time">
                    <Clock className="meta-icon" />
                    {new Date(group.dateTime).toLocaleString()}
                  </span>
                  <span className="group-place">
                    <MapPin className="meta-icon" />
                    {group.location || "Location not specified"}
                  </span>
                </div>
              </div>
            </div>
            <div className="group-actions">
              <button
                className={`group-action-btn ${joinedGroupIds.has(group.id) ? "joined" : ""}`}
                onClick={() => !joinedGroupIds.has(group.id) && toggleJoin(group.id)}
                disabled={joinedGroupIds.has(group.id)}
              >
                {joinedGroupIds.has(group.id) ? "Joined" : "Join"}
              </button>
              <button
                className="group-action-btn invite"
                onClick={() => handleDownloadInvite(group.id)}
              >
                <Download className="invite-icon" /> Download Invite
              </button>
            </div>
          </div>
        ))}
      </div>

      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Create New Group</h2>
              <button className="close-button" onClick={() => setShowCreateModal(false)}>
                <X />
              </button>
            </div>
            <form className="create-group-form" onSubmit={handleCreateGroup}>
              <div className="form-group">
                <label htmlFor="name">Group Name</label>
                <input
                  type="text"
                  id="name"
                  value={newGroup.name}
                  onChange={(e) => setNewGroup({...newGroup, name: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="location">Location</label>
                <input
                  type="text"
                  id="location"
                  value={newGroup.location}
                  onChange={(e) => setNewGroup({...newGroup, location: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="dateTime">Date and Time</label>
                <input
                  type="datetime-local"
                  id="dateTime"
                  value={newGroup.dateTime}
                  onChange={(e) => setNewGroup({...newGroup, dateTime: e.target.value})}
                  required
                />
              </div>
              <button type="submit" className="create-button">
                Create Group
              </button>
            </form>
            {feedback.message && (
              <div className={`feedback ${feedback.isError ? 'error' : 'success'}`}>
                {feedback.message}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Groups;