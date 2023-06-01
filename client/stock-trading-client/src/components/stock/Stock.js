import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './Stock.css';

const Stock = () => {
  const params = useParams();
  const key = params.stockId;
  const url = `https://www.malaysiastock.biz/Corporate-Infomation.aspx?securityCode=${key.slice(0, -3)}`;

  const [stockData, setStockData] = useState(null);

  const fetchSingleStockData = async (key) => {
    try {
      const response = await axios.get(`http://localhost:8080/stocks/${key}`);
      const stockData = response.data;
      setStockData(stockData);
    } catch (error) {
      console.error('Error fetching stock data:', error);
    }
  };

  useEffect(() => {
    if (key) {
      fetchSingleStockData(key);
    }
  }, [key]);

  return (
    <div className="stock-info-container">
      {stockData ? (
        <div>
          <h1>{stockData.name}</h1>
          <p>Symbol: {key}</p>
          <p>Price: {stockData.price}</p>
          <a href={url} target="_blank" rel="noopener noreferrer">
            Go to Stock Details
          </a>
        </div>
      ) : (
        <h1>Stock not available</h1>
      )}
    </div>
  );
};

export default Stock;
