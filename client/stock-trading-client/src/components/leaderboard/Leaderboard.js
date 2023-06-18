import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';
import { useSelector } from 'react-redux';

import './Leaderboard.css';
import leaderboard from './leaderboard.png';

const Leaderboard = () => {
  const [leaderboardData, setLeaderboardData] = useState([]);
  const [userRank, setUserRank] = useState(null);
  const userId = useSelector((state) => state.user.userId);

  useEffect(() => {
    fetchLeaderboardData();
  }, []);

  const fetchLeaderboardData = async () => {
    try {
      const response = await api.get('/api/leaderboard/top10');
      setLeaderboardData(response.data);

      if (userId === null) {
        setUserRank('N/A');
      } else {
        const response2 = await api.get(`/api/leaderboard/${userId}/rank`);
        setUserRank(response2.data);
      }
    } catch (error) {
      console.error('Error fetching leaderboard data:', error);
    }
  };

  return (
    <div className="leaderboard-container">
      <div className="leaderboard-sidepage">
        <h1 className="leaderboard-title">Leaderboard</h1>
        <img src={leaderboard} alt="Leaderboard" className="leaderboard-image" />
        <div><strong>Your current rank: {userRank}</strong></div>
      </div>
      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Username</th>
            <th>Total Points</th>
          </tr>
        </thead>
        <tbody>
          {/* Leaderboard Data */}
          {leaderboardData.map((entry, index) => (
            <tr key={entry.userId} className={index < 3 ? `highlight ${index === 0 ? 'first-place' : index === 1 ? 'second-place' : 'third-place'}` : ''}>
              <td>{index + 1}</td>
              <td>{entry.username}</td>
              <td>{entry.totalPoints.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Leaderboard;
