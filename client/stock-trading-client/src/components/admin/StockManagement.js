import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './StockManagement.css';

const StockManagement = () => {
  const [stockList, setStockList] = useState([]);
  const [deleteCode, setDeleteCode] = useState('');
  const [addCode, setAddCode] = useState('');
  const [stockTable, setStockTable] = useState([]);
  const [lastUpdateTime, setLastUpdateTime] = useState('');

  useEffect(() => {
    fetchStockList();
    fetchStockTable();

    return () => {
        clearTimeout(fetchStockTable);
    };
  }, []);

  const fetchStockList = async () => {
    try {
      const response = await axios.get('http://localhost:8080/admin/stock-list');
      setStockList(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchStockTable = async () => {
    try {
      const response = await axios.get('http://localhost:8080/stocks');
      const stockData = Array.from(response.data);
      setStockTable(stockData);
  
      const updateTime = response.headers['last-update'];
      console.log(updateTime);
      setLastUpdateTime(updateTime);
      
      setTimeout(fetchStockTable, 1 * 60 * 1000); // Fetch stock data every 1 minutes
      // setTimeout(fetchStockData, 5000); // Fetch stock data every 5 seconds
    } catch (error) {
      console.error('Error fetching stock data:', error);
      setTimeout(fetchStockTable, 5000); // Retry fetching stock data after 5 seconds in case of an error
    }
  };

  const handleDelete = async () => {
    try {
      await axios.delete('http://localhost:8080/admin/delete-stock', { data: {code: deleteCode} });
      fetchStockList();
      fetchStockTable();
      setDeleteCode('');
    } catch (error) {
      console.error(error);
    }
  };

  const handleAdd = async () => {
    try {
      await axios.post('http://localhost:8080/admin/add-stock', { code: addCode });
      fetchStockList();
      fetchStockTable();
      setAddCode('');
    } catch (error) {
      console.error(error);
    }
  };

  const getChangeClass = (value) => {
    return value < 0 ? 'negative-change' : 'positive-change';
  };

  return (
    <div className="stock-management-container">
      <h1 className="stock-management-title">Stock Management</h1>
      <div className="stock-submit">
        <div className="stock-management-form">
            <select
            className="stock-select"
            value={deleteCode}
            onChange={(e) => setDeleteCode(e.target.value)}
            >
            <option value="">Select a stock to delete</option>
            {stockList.map((code) => (
                <option key={code} value={code}>
                {code}
                </option>
            ))}
            </select>
            <button className="stock-delete-button" onClick={handleDelete}>
            Delete
            </button>
        </div>
        <div className="stock-management-form">
            <input
            type="text"
            className="stock-input"
            placeholder="Enter stock code"
            value={addCode}
            onChange={(e) => setAddCode(e.target.value)}
            />
            <button className="stock-add-button" onClick={handleAdd}>
            Add
            </button>
        </div>
      </div>
      <div className="stock-list">
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
            <tbody>
            {stockTable.map((stock) => (
                <tr key={stock.symbol}>
                <td>{stock.symbol}</td>
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

export default StockManagement;
