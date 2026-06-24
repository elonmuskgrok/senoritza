import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer } from 'react-toastify';

import LandingPage from './Components/LandingPage';
import Login from './Components/Login';
import Register from './Components/Register';
import { DashboardPage } from './Components/DashboardPage';
import { TransactionsPage } from './Components/TransactionsPage';
import { Form90cPart1Page } from './Components/Form90cPart1Page';
import { Form90cPart2Page } from './Components/Form90cPart2Page';
import { Navbar } from './Components/Navbar';

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
};

function App() {
  return (
    <Router>
      <Navbar />
      <ToastContainer />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        {/* Protected Routes */}
        <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
        <Route path="/transactions" element={<ProtectedRoute><TransactionsPage /></ProtectedRoute>} />
        <Route path="/form90c" element={<ProtectedRoute><Form90cPart1Page /></ProtectedRoute>} />
        <Route path="/form90c/upload" element={<ProtectedRoute><Form90cPart2Page /></ProtectedRoute>} />
      </Routes>
    </Router>
  );
}

export default App;

