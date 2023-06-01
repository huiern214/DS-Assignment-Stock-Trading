import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

import './StockList.css';

const StockList = () => {
  const [stocks, setStocks] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [lastUpdateTime, setLastUpdateTime] = useState('');

  const fetchStockData = async () => {
    try {
      const response = await axios.get('http://localhost:8080/stocks');
      const stockData = Array.from(response.data);
      setStocks(stockData);
  
      const updateTime = response.headers['last-update'];
      console.log(updateTime);
      setLastUpdateTime(updateTime);
      
    } catch (error) {
      console.error('Error fetching stock data:', error);
    }
  };
  
  const refreshStockData = async () => {
    try {
      await axios.get('http://localhost:8080/stocks/refresh');
      fetchStockData();
    } catch (error) {
      console.error('Error refreshing stock data:', error);
    }
  };
  
  useEffect(() => {
    fetchStockData();
    const timer = setInterval(refreshStockData, 5 * 60 * 1000);
  
    return () => {
      clearInterval(timer);
    };
  }, []);

  const handleSearch = async () => {
    try {
      const response = await axios.post('http://localhost:8080/stocks/search', {
        query: searchQuery,
      });
      const searchResults = Array.from(response.data);
      setStocks(searchResults);

    } catch (error) {
      console.error('Error searching stocks:', error);
    }
  };

  const getChangeClass = (value) => {
    return value < 0 ? 'negative-change' : 'positive-change';
  };

  return (
    <div key={lastUpdateTime}>
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
      <div className="update-time">Last Update: {lastUpdateTime}</div>
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
              <td>{stock.name}</td>
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
  );
};

export default StockList;
