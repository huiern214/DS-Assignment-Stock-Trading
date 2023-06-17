import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';
import { Link } from 'react-router-dom';

import './StockList.css';

const StockList = () => {
  const [stocks, setStocks] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [lastUpdateTime, setLastUpdateTime] = useState('');

  const fetchStockData = async () => {
    try {
      const response = await api.get('/stocks');
      const stockData = Array.from(response.data);
      setStocks(stockData);
  
      const updateTime = response.headers['last-update'];
      console.log(updateTime);
      setLastUpdateTime(updateTime);
      
      setTimeout(fetchStockData, 1 * 60 * 1000); // Fetch stock data every 1 minutes
      // setTimeout(fetchStockData, 5000); // Fetch stock data every 5 seconds
    } catch (error) {
      console.error('Error fetching stock data:', error);
      setTimeout(fetchStockData, 5000); // Retry fetching stock data after 5 seconds in case of an error
    }
  };
  
  // const refreshStockData = async () => {
  //   try {
  //     await api.get('/stocks/refresh');
  //     fetchStockData();
  //   } catch (error) {
  //     console.error('Error refreshing stock data:', error);
  //   }
  // };
  
  useEffect(() => {
    fetchStockData(); // Initial data fetch
  
    return () => {
      clearTimeout(fetchStockData);
    };
  }, []);

  const handleSearch = async () => {
    try {
      const response = await api.post('/stocks/search', {
        query: searchQuery,
      });
      const searchResults = Array.from(response.data);
      setStocks(searchResults);
      console.log(searchResults);

    } catch (error) {
      console.error('Error searching stocks:', error);
    }
  };

  const getChangeClass = (value) => {
    return value < 0 ? 'negative-change' : 'positive-change';
  };

  return (
    <div key={lastUpdateTime}>
      <div className="stock-list">
        <div className="search-container">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
            placeholder="Search by symbol or name"
          />
          <button className="search-button" type="submit" onClick={handleSearch}>
            Search
          </button>
        </div>
        <div className="update-time">Last Update: {new Date(lastUpdateTime).toLocaleString()}</div>
        <table className="stock-table">
          <thead>
            <tr>
              <th>Symbol</th>
              <th>Name</th>
              <th>Price</th>
              <th>Price Change</th>
              <th>Change %</th>
            </tr>
          </thead>
          <tbody className="table-body">
            {stocks?.map((stock) => (
              <tr key={stock.symbol}>
                <td>
                  <Link to={`/stocks/${stock.symbol}`} className="stock-link">
                    {stock.symbol}
                  </Link>
                </td>
                <td className="stock-name">{stock.name}</td>
                <td>{Number(stock.price).toFixed(2)}</td>
                <td className={getChangeClass(stock.priceChange)}>
                  {Number(stock.priceChange).toFixed(4)}
                </td>
                <td className={getChangeClass(stock.priceChangePercent)}>
                  {Number(stock.priceChangePercent).toFixed(4)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default StockList;