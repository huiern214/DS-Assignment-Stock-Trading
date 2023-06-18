import React, { useState, useEffect } from 'react';
import api from '../../api/axiosConfig';
import './UserManagement.css';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [userId, setUserId] = useState('');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await api.get('/admin/users-list');
      setUsers(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const deleteUser = async () => {
    try {
      await api.delete('/admin/delete-user', {
        data: { user_id: parseInt(userId) },
      });
      fetchUsers(); // Refresh the user list after deletion
      setUserId(''); // Clear the input field
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="user-management-container">
      <h1>User Management</h1>
      <div className="delete-user-container">
        {/* <input 
          className="user-delete-input"
          type="number"
          placeholder="Enter User ID"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        /> */}
        <select
            // className="stock-select"
            className="user-delete-input"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            >
            <option value="">Select User ID</option>
            {users.map((user) => (
                <option key={user.userId} value={user.userId}>
                    {user.userId}
                </option>
            ))}
        </select>
        <button className="stock-delete-button" onClick={deleteUser}>Delete</button>
      </div>
      {/* <table className="user-table"> */}
      <table className="stock-list">
        <thead>
          <tr>
            <th>User ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Funds</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>{user.userId}</td>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>{user.funds}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserManagement;
