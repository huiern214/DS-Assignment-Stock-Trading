import './App.css';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import StockList from './components/stocklist/StockList';
import Layout from './components/Layout';
import Navbar from './components/sidebar/Navbar';
import Stock from './components/stock/Stock';
import LoginPage from './components/login/LoginPage';
import Profile from './components/profile/Profile';
import Dashboard from './components/dashboard/Dashboard';
import UserManagement from './components/admin/UserManagement';
import StockManagement from './components/admin/StockManagement';
import News from './components/news/News';

function App() {

  const userId = useSelector((state) => state.user.userId);

  const renderRestrictedRoute = (component, path) => {
    if (userId < 0) {
      return <Route path={path} element={component} />;
    }
    return <Route path={path} element={<Navigate to="/login" replace />} />;
  };

  return (
    <div className="App">
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<Layout/>}>
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/stocks" element={<StockList />} ></Route>
            <Route path="/stocks/:stockId" element={<Stock />}></Route>
            <Route path="/news" element={<News />}></Route>
            <Route path="/dashboard" element={<Dashboard />}></Route>
            <Route path="/profile" element={<Profile />}></Route>
            {renderRestrictedRoute(<UserManagement />, '/user_management')}
            {renderRestrictedRoute(<StockManagement />, '/stock_management')}
            <Route path="*" element={<h1>Not Found</h1>}></Route>
          </Route>
      </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
