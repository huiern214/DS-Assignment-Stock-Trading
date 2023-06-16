import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import axios from 'axios';
import Popup from 'reactjs-popup';
import './Stock.css';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';


const Stock = () => {
  const params = useParams();
  const key = params.stockId;
  const url = `https://www.malaysiastock.biz/Corporate-Infomation.aspx?securityCode=${key.slice(0, -3)}`;
  const userId = useSelector((state) => state.user.userId);

  const [lastUpdateTime, setLastUpdateTime] = useState('');
  const [toggleState, setToggleState] = useState(1);
  
  const [bidData, setBidData] = useState([]);
  const [askData, setAskData] = useState([]);

  const [stockData, setStockData] = useState(null);
  const [tradeAmount, setTradeAmount] = useState('');
  const [numberOfShares, setNumberOfShares] = useState('');

  useEffect(() => {
    if (key) {
      fetchSingleStockData(key);
      fetchMarketDepth(key);
    }
  }, [key]);

  const fetchSingleStockData = async (key) => {
    try {
      const response = await axios.get(`http://localhost:8080/stocks/${key}`);
      const stockData = response.data;
      setStockData(stockData);

      const updateTime = response.headers['last-update'];
      console.log(updateTime);
      setLastUpdateTime(updateTime);

    } catch (error) {
      console.error('Error fetching stock data:', error);
    }
  };

  const toggleTab = (index) => {
    setToggleState(index);
  };

  const fetchMarketDepth = async (key) => {
    try {
      const [bidResponse, askResponse] = await Promise.all([
        axios.get(`http://localhost:8080/stocks/bid-data/${key}`),
        axios.get(`http://localhost:8080/stocks/ask-data/${key}`),
      ]);

      setBidData(bidResponse.data);
      setAskData(askResponse.data);

    } catch (error) {
      console.error('Error fetching market depth data:', error);
    }
  };

  const handleTradeAmountChange = (e) => {
    const amount = e.target.value;

    if (!isNaN(amount) && amount >= 0) {
      setTradeAmount(amount);
    }
  };

  const handleNumberOfSharesChange = (e) => {
    const shares = e.target.value;

    if (!isNaN(shares) && shares >= 0) {
      setNumberOfShares(shares);
    }
  };

  // const handleTradeStock = () => {
  //   if (!tradeAmount || !numberOfShares) {
  //     console.log('Please enter a value for both fields.');
  //     return;
  //   }
  //     // Perform the buy action with the bid amount and number of shares
  //     console.log('Trade stock:', tradeAmount, numberOfShares);
  //     // You can add your buy logic here

  //     // Reset the input fields
  //     setTradeAmount('');
  //     setNumberOfShares('');
  // };
  const handleTradeStock = async (action) => {
    if (!tradeAmount || !numberOfShares) {
      console.log('Please enter a value for both fields.');
      return;
    }
  
    try {
      if (action === 'buy') {
        await axios.post('http://localhost:8080/api/buysell/buy', {
          stockSymbol: key,
          desiredPrice: tradeAmount,
          desiredQuantity: numberOfShares,
          buyerId: userId, // Replace with the actual buyer ID
        });
        console.log('Stock bought successfully.');
        toast.success('Stock bought successfully.');
      } else if (action === 'sell') {
        await axios.post('http://localhost:8080/api/buysell/sell', {
          stockSymbol: key,
          desiredPrice: tradeAmount,
          desiredQuantity: numberOfShares,
          sellerId: userId, // Replace with the actual seller ID
        });
        console.log('Stock sold successfully.');
        toast.success('Stock sold successfully.');
      }
  
      // Reset the input fields
      setTradeAmount('');
      setNumberOfShares('');
    } catch (error) {
      console.error('Error trading stock:', error);
      if (error.response && error.response.data) {
        toast.error(`${error.response.data}`);
      } else {
        toast.error('Failed to trade the stock.');
      }
    }
  };

  const getChangeClass = (value) => {
    return value < 0 ? 'negative-change' : 'positive-change';
  };

  return (
    <div className="stock-info-container">
      <ToastContainer />
      {stockData ? (
        <div>
          <div className="stock-header">
            <div className="stock-info">
              <h1 className='name'>{stockData.name}</h1>
              <p className="symbol">{key}</p>
              <div className="price-change">
                <p className="price">{Number(stockData.price).toFixed(2)}</p>
                <p className={getChangeClass(stockData.priceChange)}>{Number(stockData.priceChange).toFixed(4)}</p>
                <p className={getChangeClass(stockData.priceChangePercent)}>({Number(stockData.priceChangePercent).toFixed(4)})</p>
              </div>
              <p className="updated-time">Last Update: {new Date(lastUpdateTime).toLocaleString()}</p>
            </div>
            <div className="stock-actions">
              <Popup trigger={<button className="buy-button">Buy</button>} modal nested>
                {(close) => (
                  <div className="modal">
                    <div className="modal-content">
                      <h2>Buy Stock</h2>
                      <label>Bid Amount:</label>
                      <div className="input-wrapper">
                        <input
                          type="number"
                          value={tradeAmount}
                          onChange={handleTradeAmountChange}
                          step="0.0001"
                        />
                      </div>
                      <label>Qty (x100):</label>
                      <div className="input-wrapper">
                        <input
                          type="number"
                          value={numberOfShares}
                          onChange={handleNumberOfSharesChange}
                          step="1"
                          min="1"
                        />
                      </div>
                      <div className="modal-buttons">
                        <button onClick={() => { handleTradeStock('buy'); close(); }} disabled={!tradeAmount || !numberOfShares}>Buy</button>
                        <button onClick={close}>Cancel</button>
                      </div>
                    </div>
                  </div>
                )}
              </Popup>
              <Popup trigger={<button className="sell-button">Sell</button>} modal nested>
                {(close) => (
                  <div className="modal">
                    <div className="modal-content">
                      <h2>Sell Stock</h2>
                      <label>Ask Amount:</label>
                      <div className="input-wrapper">
                        <input
                          type="number"
                          value={tradeAmount}
                          onChange={handleTradeAmountChange}
                          step="0.0001"
                        />
                      </div>
                      <label>Qty (x100):</label>
                      <div className="input-wrapper">
                        <input
                          type="number"
                          value={numberOfShares}
                          onChange={handleNumberOfSharesChange}
                          step="1"
                        />
                      </div>
                      <div className="modal-buttons">
                        <button onClick={() => { handleTradeStock('sell'); close(); }} disabled={!tradeAmount || !numberOfShares}>Sell</button>
                        <button onClick={close}>Cancel</button>
                      </div>
                    </div>
                  </div>
                )}
              </Popup>
            </div>
          </div>
          <div className="container">
            <div className="bloc-tabs">
              <button
                className={toggleState === 1 ? "tabs active-tabs" : "tabs"}
                onClick={() => toggleTab(1)}
              >
                Overview
              </button>
              <button
                className={toggleState === 2 ? "tabs active-tabs" : "tabs"}
                onClick={() => toggleTab(2)}
              >
                Market Depth
              </button>
              <button
                className={toggleState === 3 ? "tabs active-tabs" : "tabs"}
                onClick={() => toggleTab(3)}
              >
                News
              </button>
            </div>

            <div className="content-tabs">
              <div
                className={toggleState === 1 ? "content  active-content" : "content"}
              >
                <h2>Overview</h2>
                <hr />
                <div className="stock-overview">
                  <a href={url} target="_blank" rel="noopener noreferrer" className="link">
                    Go to Stock Details
                  </a>
                  <iframe src={url} title="Stock Details" className="stock-preview-iframe" />
                </div>
              </div>

              <div
                className={toggleState === 2 ? "content  active-content" : "content"}
              >
                <h2>Market Depth</h2>
                <hr />
                <div className="market-depth">
                  <table className="stock-table">
                    <thead>
                      <tr>
                        <th>No. of Acc</th>
                        <th>Bid Qty</th>
                        <th>Bid</th>
                        <th>Ask</th>
                        <th>Ask Qty</th>
                        <th>No. of Acc</th>
                      </tr>
                    </thead>
                    <tbody className="table-body">
                      {bidData.map((bid, index) => (
                        <tr key={index}>
                          <td>{bid.noOfAcc}</td>
                          <td>{bid.qty}</td>
                          <td>{bid.price}</td>
                          <td>{askData[index].price}</td>
                          <td>{askData[index].qty}</td>
                          <td>{askData[index].noOfAcc}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              <div
                className={toggleState === 3 ? "content  active-content" : "content"}
              >
                <h2>News</h2>
                <hr />
                <p>
                  Lorem ipsum dolor sit amet, consectetur adipisicing elit. Eos sed
                  nostrum rerum laudantium totam unde adipisci incidunt modi alias!
                  Accusamus in quia odit aspernatur provident et ad vel distinctio
                  recusandae totam quidem repudiandae omnis veritatis nostrum
                  laboriosam architecto optio rem, dignissimos voluptatum beatae
                  aperiam voluptatem atque. Beatae rerum dolores sunt.
                </p>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <h1>Stock not available</h1>
      )}
    </div>
  );
};

export default Stock;
