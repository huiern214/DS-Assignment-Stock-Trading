import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
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
      const response = await api.get(`/stocks/${key}`);
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
        api.get(`/stocks/bid-data/${key}`),
        api.get(`/stocks/ask-data/${key}`),
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

  const handleTradeStock = async (action) => {

    // Check if it is within market hours
    if (!isWithinMarketHours()) {
      console.log('Trading is only allowed during market hours.');
      const currentTime = new Date().toLocaleTimeString();
      toast.error('Trading is only allowed during market hours.');
      return;
    }

    if (!tradeAmount || !numberOfShares) {
      console.log('Please enter a value for both fields.');
      toast.error('Please enter a value for both fields.');
      return;
    }

    if (userId === null) {
      console.log('Please login to trade.');
      toast.error('Please login to trade.');
      return;
    }
  
    try {
      if (action === 'buy') {
        await api.post('/api/buysell/buy', {
          stockSymbol: key,
          desiredPrice: tradeAmount,
          desiredQuantity: numberOfShares,
          buyerId: userId, // Replace with the actual buyer ID
        });
        console.log('Stock bought successfully.');
        toast.success('Stock bought successfully.');
        // // Send email notification
        // await api.post(`/api/email/${userId}`, {
        //   subject: 'Stock Purchase Confirmation',
        //   body: `You have successfully purchased ${numberOfShares} shares of ${key}.`,
        // });
        // window.location.reload();
        try {
          // Send email notification
          await api.post(`/api/email/${userId}`, {
            subject: 'Stock Purchase Confirmation',
            body: `You have successfully purchased ${numberOfShares} shares of ${key}.`,
          });
        } catch (error) {
          console.error('Error sending email notification:', error);
        }
      } else if (action === 'sell') {
        await api.post('/api/buysell/sell', {
          stockSymbol: key,
          desiredPrice: tradeAmount,
          desiredQuantity: numberOfShares,
          sellerId: userId, // Replace with the actual seller ID
        });
        console.log('Stock sold successfully.');
        toast.success('Stock sold successfully.');

        // // Send email notification
        // await api.post(`/api/email/${userId}`, {
        //   subject: 'Stock Sale Confirmation',
        //   body: `You have successfully sold ${numberOfShares} shares of ${key}.`,
        // });
        try {
          // Send email notification
          await api.post(`/api/email/${userId}`, {
            subject: 'Stock Sale Confirmation',
            body: `You have successfully sold ${numberOfShares} shares of ${key}.`,
          });
        } catch (error) {
          console.error('Error sending email notification:', error);
        }
        window.location.reload();
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

  const isWithinMarketHours = () => {
    const now = new Date();
    const dayOfWeek = now.getDay();
    const hour = now.getHours();
    const minute = now.getMinutes();

    // Check if it is a weekday (Monday to Friday)
    if (dayOfWeek === 0 || dayOfWeek === 6) {
      return false;
    }

    // Check if it is within market hours (9:00 AM - 12:30 PM and 2:30 PM - 5:00 PM)
    if ((hour >= 9 && hour < 12) || (hour === 12 && minute <= 30) || (hour >= 14 && hour < 17)) {
      return true;
    }

    return false;
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
              <p className="updated-time">System stock qty: {stockData.systemQuantity}</p>
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
                          max="99999"
                        />
                      </div>
                      <p>Total: {(tradeAmount * numberOfShares * 100).toFixed(2)}</p>
                      <div className="modal-buttons">
                        <button onClick={() => { handleTradeStock('buy'); close(); }} disabled={!tradeAmount || !numberOfShares || numberOfShares > 99999}>Buy</button>
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
                          min="1"
                          max="99999"
                        />
                      </div>
                      <p>Total: {(tradeAmount * numberOfShares * 100).toFixed(2)}</p>
                      <div className="modal-buttons">
                        <button onClick={() => { handleTradeStock('sell'); close(); }} disabled={!tradeAmount || !numberOfShares || numberOfShares > 99999}>Sell</button>
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
                      {bidData.map((bid, index) => {
                        const ask = askData[index] || {}; // Set a default empty object if askData[index] is undefined

                        return (
                          <tr key={index}>
                            <td>{bid.noOfAcc}</td>
                            <td>{bid.qty}</td>
                            <td>{bid.price}</td>
                            <td>{ask.price || '-'}</td> {/* Display a dash if ask.price is undefined or empty */}
                            <td>{ask.qty || '-'}</td> {/* Display a dash if ask.qty is undefined or empty */}
                            <td>{ask.noOfAcc || '-'}</td> {/* Display a dash if ask.noOfAcc is undefined or empty */}
                          </tr>
                        );
                      })}
                      {askData.slice(bidData.length).map((ask, index) => (
                        <tr key={index + bidData.length}>
                          <td>-</td> {/* Display a dash for bidData columns */}
                          <td>-</td> {/* Display a dash for bidData columns */}
                          <td>-</td> {/* Display a dash for bidData columns */}
                          <td>{ask.price}</td>
                          <td>{ask.qty}</td>
                          <td>{ask.noOfAcc}</td>
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
