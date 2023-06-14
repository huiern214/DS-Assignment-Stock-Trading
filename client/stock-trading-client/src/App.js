import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import StockList from './components/stocklist/StockList';
import Layout from './components/Layout';
import Navbar from './components/sidebar/Navbar';
import Stock from './components/stock/Stock';
import LoginPage from './components/login/LoginPage';
import Profile from './components/profile/Profile';
import Dashboard from './components/dashboard/Dashboard';

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<Layout/>}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/" element={<StockList />} ></Route>
            <Route path="/stocks/:stockId" element={<Stock />}></Route>
            <Route path="/dashboard" element={<Dashboard />}></Route>
            <Route path="/profile" element={<Profile />}></Route>
            <Route path="*" element={<h1>Not Found</h1>}></Route>
            {/* <Route path="/Reviews/:movieId" element ={<Reviews getMovieData={getMovieData} movie={movie} reviews={reviews} setReviews={setReviews} />}></Route>
            <Route path="*" element = {<NotFound/>}></Route> */}
          </Route>
      </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
