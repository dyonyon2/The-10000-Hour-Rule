import logo from './logo.svg';
import './App.css';
import {BrowserRouter, Routes, Route, Switch} from "react-router-dom"
import Home from './pages/Home';
import Login from './pages/Login'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' exact Component={Home}/>
        <Route path='/login' exact Component={Login}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
