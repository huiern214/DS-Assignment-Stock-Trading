import React, { useEffect, useState } from 'react';
import api from '../../api/axiosConfig';
import './News.css';

const News = () => {
  const [newsList, setNewsList] = useState([]);

  useEffect(() => {
    fetchNews();
  }, []);

  const fetchNews = async () => {
    try {
      const response = await api.get('/news');
      setNewsList(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="news-container">
      <h2 className="news-header">Latest News in Malaysia</h2>
      {newsList.map((news, index) => (
        <div className="news-item" key={index}>
          <img src={news.imageUrl} alt={news.title} className="news-image" />
          <div className="news-details">
            <h3 className="news-title">{news.title}</h3>
            <p className="news-description">{news.description}</p>
            <p className="news-published-at">Published At: {new Date(news.publishedAt).toLocaleString()}</p>
            <p className="news-source">Source: {news.source}</p>
            <a href={news.url} target="_blank" rel="noopener noreferrer" className="news-read-more">Read More</a>
          </div>
        </div>
      ))}
    </div>
  );
};

export default News;
