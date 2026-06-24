import React from 'react';
import { useNavigate } from 'react-router-dom';

export const DashboardPage = () => {
  const navigate = useNavigate();
  // We use user.name now due to the single table schema refactor
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  return (
    <div className="container mt-5">
      {/* Header Section */}
      <div className="d-flex justify-content-between align-items-center mb-4 p-4 shadow-sm bg-light rounded border">
        <div>
          <h2 className="mb-0 fw-bold">
            Welcome back, {user.name || 'User'}!
          </h2>
          <p className="text-muted mb-0">Manage your taxes effortlessly.</p>
        </div>
      </div>
      
      {/* Cards Section */}
      <div className="row g-4">
        <div className="col-md-6">
          <div className="card h-100 shadow-sm border rounded" onClick={() => navigate('/transactions')} style={{ cursor: 'pointer' }}>
            <div className="card-body p-5 text-center">
              <div className="mb-4 text-primary" style={{ fontSize: '3rem' }}>
                <i className="bi bi-receipt-cutoff"></i>
              </div>
              <h4 className="card-title fw-bold">Transactions</h4>
              <p className="card-text text-muted mb-4">
                View, filter, and download your yearly transactions in JSON or CSV format.
              </p>
              <button className="btn btn-primary btn-lg w-100">
                View Transactions <i className="bi bi-arrow-right ms-2"></i>
              </button>
            </div>
          </div>
        </div>
        
        <div className="col-md-6">
          <div className="card h-100 shadow-sm border rounded" onClick={() => navigate('/form90c')} style={{ cursor: 'pointer' }}>
            <div className="card-body p-5 text-center">
              <div className="mb-4 text-success" style={{ fontSize: '3rem' }}>
                <i className="bi bi-file-earmark-check"></i>
              </div>
              <h4 className="card-title fw-bold">Form 90C</h4>
              <p className="card-text text-muted mb-4">
                File your Form 90C based on your transactions. Save drafts and upload documents securely.
              </p>
              <button className="btn btn-success btn-lg w-100">
                Start Form 90C <i className="bi bi-arrow-right ms-2"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
