import logo from './logo.svg';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import StockList from './components/stocklist/StockList';
import Layout from './components/Layout';

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <h1>Stock Trading App</h1>
        <Routes>
          <Route path="/" element={<Layout/>}>
            <Route path="/home" element={<StockList />} ></Route>
            {/* <Route path="/Trailer/:ytTrailerId" element={<Trailer/>}></Route>
            <Route path="/Reviews/:movieId" element ={<Reviews getMovieData={getMovieData} movie={movie} reviews={reviews} setReviews={setReviews} />}></Route>
            <Route path="*" element = {<NotFound/>}></Route> */}
          </Route>
      </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
