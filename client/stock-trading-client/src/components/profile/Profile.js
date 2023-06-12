import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './Profile.css';
import { useSelector } from 'react-redux';

function Profile() {
  const [profileData, setProfileData] = useState(null);
  const userId = useSelector((state) => state.user.userId);

  useEffect(() => {
    fetchUserProfile(userId);
  }, [userId]);

  const fetchUserProfile = async (userId) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/users/${userId}`);
      if (response.status === 200) {
        const profileData = response.data;
        setProfileData(profileData);
      }
    } catch (error) {
      console.log(error);
    }
  };

  if (!profileData) {
    return <div>Loading profile...</div>;
  }

  return (
    <div className="profile-container">
      <h2 className="profile-title">User Profile</h2>
      <div className="profile-item">
        <label>User ID:</label>
        <span>{profileData.userId}</span>
      </div>
      <div className="profile-item">
        <label>Username:</label>
        <span>{profileData.username}</span>
      </div>
      <div className="profile-item">
        <label>Email:</label>
        <span>{profileData.email}</span>
      </div>
      <div className="profile-item">
        <label>Funds:</label>
        <span>{profileData.funds}</span>
      </div>
      
      <h2 className="profile-title">Portfolio</h2>
      <div className="profile-item">
        <label>Holdings:</label>
        <span>{JSON.stringify(profileData.portfolio.holdings)}</span>
      </div>
      <div className="profile-item">
        <label>Value:</label>
        <span>{profileData.portfolio.value}</span>
      </div>
    </div>
  );
}

export default Profile;
