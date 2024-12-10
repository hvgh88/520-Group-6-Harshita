import React, { useState, useEffect, useRef } from "react";
import { Search, Send } from "lucide-react";
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import "./ChatPage.css";
import {useNavigate} from "react-router-dom";

const ChatPage = () => {
  const [message, setMessage] = useState("");
  const [groupSearchQuery, setGroupSearchQuery] = useState("");
  const [chatSearchQuery, setChatSearchQuery] = useState("");
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [userGroups, setUserGroups] = useState([]);
  const [stompClient, setStompClient] = useState(null);
  const [messages, setMessages] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const userId = localStorage.getItem('userId');
  const navigate = useNavigate();

  const messagesEndRef = useRef(null);

  useEffect(() => {
    fetchUserGroups();
    fetchCurrentUser();
    if (!userId) {
      navigate('/login');
    }
  }, []);

  useEffect(() => {
    if (selectedGroup) {
      scrollToBottom();
      connectWebSocket();
    }
  }, [selectedGroup]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const fetchUserGroups = async () => {
    try {
      const response = await fetch(`http://localhost:8080/user/group-ids/${userId}`);
      if (!response.ok) {
        throw new Error('Failed to fetch user group IDs');
      }
      const groupIds = await response.json();
      const groupDetails = await Promise.all(groupIds.map(fetchGroupDetails));
      setUserGroups(groupDetails.filter(group => group !== null));
    } catch (error) {
      console.error('Error fetching user groups:', error);
    }
  };

  const fetchCurrentUser = async () => {
    try {
      const response = await fetch(`http://localhost:8080/user/${userId}`);
      if (!response.ok) {
        throw new Error('Failed to fetch user details');
      }
      const user = await response.json();
      setCurrentUser(user);
    } catch (error) {
      console.error('Error fetching current user:', error);
    }
  };

  const fetchGroupDetails = async (groupId) => {
    try {
      const response = await fetch('http://localhost:9200/groups/_search/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          query: { term: { id: groupId } }
        }),
      });
      if (!response.ok) {
        throw new Error('Failed to fetch group details');
      }
      const data = await response.json();
      return data.hits.hits[0]._source;
    } catch (error) {
      console.error(`Error fetching details for group ${groupId}:`, error);
      return null;
    }
  };

  const searchGroups = () => {
    if (!groupSearchQuery.trim()) {
      setSearchResults([]);
      return;
    }
    const filteredGroups = userGroups.filter((group) =>
      group.name.toLowerCase().includes(groupSearchQuery.toLowerCase())
    );
    setSearchResults(filteredGroups);
  };

  const connectWebSocket = () => {
    if (stompClient) stompClient.disconnect();

    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);

    client.connect({}, () => {
      console.log('Connected to WebSocket');
      setStompClient(client);

      client.subscribe(`/topic/messages/${selectedGroup.id}`, handleIncomingMessage);

      fetchMessagesFromElasticsearch(selectedGroup.id);
    });
  };

  const fetchMessagesFromElasticsearch = async (groupId) => {
    try {
      const response = await fetch('http://localhost:9200/messages/_search', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          query: { match: { "group.id": groupId } },
          sort: [{ "_id": { "order": "asc" } }]
        })
      });
      const data = await response.json();
      console.log('Received messages:', data);
      
      const fetchedMessages = data.hits.hits.map(hit => ({
        id: hit._id,
        senderUsername: hit._source.sender.username || hit._source.senderUsername,
        content: hit._source.content,
        timestamp: hit._source.timestamp,
        sortId: hit.sort[0]
      }));
      
      fetchedMessages.sort((a, b) => a.sortId - b.sortId);
      setMessages(fetchedMessages);
    } catch (error) {
      console.error('Error fetching messages from Elasticsearch:', error);
    }
  };

  const handleIncomingMessage = (message) => {
    const messageBody = JSON.parse(message.body);
    setMessages(prevMessages => [...prevMessages, messageBody]);
    scrollToBottom();
  };

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (message.trim() && selectedGroup && stompClient && currentUser) {
      stompClient.send(`/app/chat/${selectedGroup.id}`, {}, JSON.stringify({
        content: message,
        senderId: currentUser.id,
        groupId: selectedGroup.id,
        senderUsername: currentUser.username
      }));
      setMessage("");
    }
  };

  const handleGroupSearchChange = (e) => {
    setGroupSearchQuery(e.target.value);
  };

  const handleChatSearchChange = (e) => {
    setChatSearchQuery(e.target.value);
  };

  const handleSearchMessages = () => {
    if (!chatSearchQuery.trim() || !messages.length) {
      setSearchResults([]);
      return;
    }
    const results = messages.filter((msg) =>
      msg.content.toLowerCase().includes(chatSearchQuery.toLowerCase())
    );
    setSearchResults(results);
  };

  return (
    <div className="chat-layout">
      <div className="chat-sidebar">
        <div className="search-container">
          <Search className="search-icon" />
          <input
            type="text"
            placeholder="Search for groups..."
            className="search-input"
            value={groupSearchQuery}
            onChange={handleGroupSearchChange}
            onKeyUp={(e) => e.key === 'Enter' && searchGroups()}
          />
        </div>
        <div className="recent-chats">
          <h2>Groups</h2>
          {(groupSearchQuery ? searchResults : userGroups).map((group) => (
            <div
              key={group.id}
              className={`chat-item ${selectedGroup?.id === group.id ? "active" : ""}`}
              onClick={() => setSelectedGroup(group)}
            >
              <div className="chat-avatar">{group.name[0]}</div>
              <div className="chat-item-info">
                <div className="chat-item-name">{group.name}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
      
      <div className="chat-main">
        {selectedGroup ? (
          <>
            <div className="chat-header">
              <h2>{selectedGroup.name}</h2>
            </div>
            <div className="chat-search-container">
              <Search className="chat-search-icon" />
              <input
                type="text"
                placeholder="Search within chat..."
                className="chat-search-input"
                value={chatSearchQuery}
                onChange={handleChatSearchChange}
                onKeyUp={(e) => e.key === 'Enter' && handleSearchMessages()}
              />
            </div>
            <div className="messages-container">
              {(chatSearchQuery ? searchResults : messages).map((msg) => (
                <div key={msg.id} className={`message ${msg.senderUsername === currentUser?.username ? "sent" : "received"}`}>
                  <div className="message-content">
                    <div className="message-sender">{msg.senderUsername}</div>
                    <div className="message-text">{msg.content}</div>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>
            <form onSubmit={handleSendMessage} className="message-input-container">
              <input
                type="text"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Type a message..."
                className="message-input"
              />
              <button type="submit" className="send-button"><Send /></button>
            </form>
          </>
        ) : (
          <div className="no-chat-selected">Select a group to start chatting</div>
        )}
      </div>
    </div>
  );
};

export default ChatPage;