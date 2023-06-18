import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';
import { useSelector } from 'react-redux';

import './Orders.css';

const Orders = () => {
  const userId = useSelector((state) => state.user.userId);
  const [sellOrders, setSellOrders] = useState([]);
  const [buyOrders, setBuyOrders] = useState([]);

  useEffect(() => {
    const fetchOrders = async () => {
      if (userId === null) {
        return <p>Loading orders...</p>;
      }
      try {
        const sellResponse = await api.get(`/orders/user/${userId}`);
        setSellOrders(sellResponse.data.filter((order) => order.orderType === 'SELL'));
        const buyResponse = await api.get(`/orders/user/${userId}`);
        setBuyOrders(buyResponse.data.filter((order) => order.orderType === 'BUY'));
      } catch (error) {
        console.log('Error fetching orders:', error);
      }
    };

    fetchOrders();
  }, [userId]);

  const handleDeleteOrder = async (orderId) => {
    if (userId === null) {
      return <p>Loading...</p>;
    }
    try {
      await api.delete('/orders/delete-order', {
        data: { order_id: orderId },
      });
      setSellOrders(sellOrders.filter((order) => order.orderId !== orderId));
      setBuyOrders(buyOrders.filter((order) => order.orderId !== orderId));
    } catch (error) {
      console.log('Error deleting order:', error);
    }
  };  

  return (
    <div className="order-container">
      <h1>Orders</h1>
      <div className="order-section">
      <h2 className="order-title">SELL</h2>
      <table className="order-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Stock Symbol</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {sellOrders.map((order) => (
            <tr key={order.orderId}>
              <td>{order.orderId}</td>
              <td>
                <a className="stock-link" href={`/stocks/${order.stockSymbol}`}>
                  {order.stockSymbol}
                </a>
              </td>
              <td>{order.quantity}</td>
              <td>{order.price}</td>
              <td>
                <button className="order-delete-button" onClick={() => handleDeleteOrder(order.orderId)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2 className="order-title">BUY</h2>
      <table className="order-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Stock Symbol</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {buyOrders.map((order) => (
            <tr key={order.orderId}>
              <td>{order.orderId}</td>
              <td>
                <a className="stock-link" href={`/stocks/${order.stockSymbol}`}>
                  {order.stockSymbol}
                </a>
              </td>
              <td>{order.quantity}</td>
              <td>{order.price}</td>
              <td>
                <button className="order-delete-button" onClick={() => handleDeleteOrder(order.orderId)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
};

export default Orders;
