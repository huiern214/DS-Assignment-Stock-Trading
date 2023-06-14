import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import './Dashboard.css';

const Dashboard = () => {
  const userId = useSelector((state) => state.user.userId);
  const [dashboardData, setDashboardData] = useState(null);
  const [portfolioValue, setPortfolioValue] = useState(null);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        const dashboardResponse = await axios.get(`http://localhost:8080/dashboard/${userId}`);
        const portfolioResponse = await axios.get(`http://localhost:8080/dashboard/${userId}/portfolio`);
        setDashboardData(dashboardResponse.data);
        setPortfolioValue(portfolioResponse.data.value);
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, [userId]);

  const getPnLColor = (value) => {
    if (value < 0) {
      return 'red';
    } else if (value > 0) {
      return 'green';
    } else {
      return 'black';
    }
  };

  return (
    <div className="dashboard-container">
      {dashboardData && portfolioValue ? (
        <>
          <h1>Trading Dashboard</h1>

          {/* Total PnL and Points */}
          <div className="dashboard-section">
            <div className="dashboard-stats">
              <div>
              <h2>Total Realized P/L</h2>
                <p className="stats-text" style={{ color: getPnLColor(dashboardData.totalPnL) }}>MYR {dashboardData.totalPnL.toFixed(2)} </p>
                <span className="small-text" >({(dashboardData.totalPnLPercentage).toFixed(2)}%)</span>
              </div>
              <div>
                <h2>Total Unrealized P/L</h2>
                <p className="stats-text" style={{ color: getPnLColor(dashboardData.unrealisedPnL) }}>MYR {dashboardData.unrealisedPnL.toFixed(2)}</p>
                <span className="small-text">({(dashboardData.unrealisedPnLPercentage).toFixed(2)}%)</span>
              </div>
              <div>
                <h2>Total Points</h2>
                <p className="points">{dashboardData.totalPoints.toFixed(2)} pts</p>
                <p className="small-text">Rank: null</p>
              </div>
              <div>
                <h2>Total Market Value</h2>
                <p className="stats-text">MYR {portfolioValue.toFixed(2)}</p> 
              </div>
            </div>
          </div>

          {/* Open Positions */}
          <div className="dashboard-section">
            <h2>Open Positions</h2>
            <table>
              <thead>
                <tr>
                  <th>Symbol</th>
                  <th>Qty Sold</th>
                  <th>Entry Price</th>
                  <th>Market Price</th>
                  <th>Entry Time</th>
                  <th>Unrealized P/L (ROI)</th>
                </tr>
              </thead>
              <tbody>
                {dashboardData.openPositions.map((position) => (
                  <tr key={position.symbol}>
                    <td>{position.symbol}</td>
                    <td>{position.qtySold}</td>
                    <td>{position.entryPrice.toFixed(2)}</td>
                    <td>{position.exitPrice.toFixed(2)}</td>
                    <td>{position.entryTime}</td>
                    <td>
                        <span
                            className={`pnl-box ${position.pnL < 0 ? 'red' : 'green'}`}
                        >
                            {position.pnL.toFixed(2)} ({position.roi.toFixed(2)}%)
                        </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Trade History */}
          <div className="dashboard-section">
            <h2>Trade History</h2>
            <table>
              <thead>
                <tr>
                  <th>Symbol</th>
                  <th>Qty Sold</th>
                  <th>Entry Price</th>
                  <th>Exit Price</th>
                  <th>Entry Time</th>
                  <th>Exit Time</th>
                  <th>Realized P/L (ROI)</th>
                </tr>
              </thead>
              <tbody>
                {dashboardData.tradeHistory.map((trade) => (
                  <tr key={trade.symbol}>
                    <td>{trade.symbol}</td>
                    <td>{trade.qtySold}</td>
                    <td>{trade.entryPrice.toFixed(2)}</td>
                    <td>{trade.exitPrice.toFixed(2)}</td>
                    <td>{trade.entryTime}</td>
                    <td>{trade.exitTime}</td>
                    <td>
                        <span
                            className={`pnl-box ${trade.pnL < 0 ? 'red' : 'green'}`}
                        >
                            {trade.pnL.toFixed(2)} ({trade.roi.toFixed(2)}%)
                        </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      ) : (
        <p>Loading dashboard data...</p>
      )}
    </div>
  );
};

export default Dashboard;
