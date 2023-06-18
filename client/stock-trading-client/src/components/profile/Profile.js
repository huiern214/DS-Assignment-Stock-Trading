import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';
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
      if (userId === null) {
        return <div>Loading profile...</div>;
      }
      const response = await api.get(`/api/users/${userId}`);
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
      <div className="profile-header">
        <h2 className="profile-title">User Profile</h2>
      </div>
      <div className="profile-content">
        <table className="profile-table">
          <tbody>
            <tr>
              <td><strong>User ID:</strong></td>
              <td>{profileData.userId}</td>
            </tr>
            <tr>
              <td><strong>Username:</strong></td>
              <td>{profileData.username}</td>
            </tr>
            <tr>
              <td><strong>Email:</strong></td>
              <td>{profileData.email}</td>
            </tr>
            <tr>
              <td><strong>Funds:</strong></td>
              <td>{profileData.funds}</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div className="profile-header">
        <h2 className="profile-title">Portfolio</h2>
      </div>
      <div className="profile-content">
        <div className="profile-item">
          <label>Total Market Value: </label>
          <span>MYR {profileData.portfolio.value? profileData.portfolio.value.toFixed(2) : '0.00'}</span>
        </div>
        <div className="portfolio-table">
          <table>
            <thead>
              <tr>
                <th className="symbol-stock-column">Stock</th>
                <th>Quantity</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(profileData.portfolio.holdings).map(([stock, quantity]) => (
                <tr key={stock}>
                  <td className="symbol-stock-column">{stock}</td>
                  <td>{quantity}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Profile;
