import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { transactionApi } from '../api/transactionApi';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { toast } from 'react-toastify';

export const DashboardPage = () => {
  const navigate = useNavigate();
  // We use user.name now due to the single table schema refactor
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  const getCurrentFY = () => {
    const today = new Date();
    const month = today.getMonth() + 1;
    const year = today.getFullYear();
    if (month >= 4) {
      return `${year}-${year + 1}`;
    } else {
      return `${year - 1}-${year}`;
    }
  };

  const [financialYear, setFinancialYear] = useState('');
  const [financialYears, setFinancialYears] = useState<string[]>([]);
  const [summary, setSummary] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initDashboard = async () => {
      try {
        const years = await transactionApi.getAvailableFinancialYears();
        setFinancialYears(years);
        if (years.length > 0) {
          const current = getCurrentFY();
          if (years.includes(current)) {
            setFinancialYear(current);
          } else {
            setFinancialYear(years[0]);
          }
        } else {
          setLoading(false);
        }
      } catch (err) {
        console.error('Failed to fetch years', err);
        setLoading(false);
      }
    };
    initDashboard();
  }, []);

  useEffect(() => {
    if (!financialYear) return;
    const fetchSummary = async () => {
      setLoading(true);
      try {
        const data = await transactionApi.getDashboardSummary(financialYear);
        setSummary(data);
      } catch (error) {
        console.error('Failed to fetch dashboard summary', error);
      } finally {
        setLoading(false);
      }
    };
    fetchSummary();
  }, [financialYear]);

  // SVG Progress Ring helper
  const ProgressRing = ({ radius, stroke, progress, color }: any) => {
    const normalizedRadius = radius - stroke * 2;
    const circumference = normalizedRadius * 2 * Math.PI;
    const strokeDashoffset = circumference - (progress / 100) * circumference;
  
    return (
      <svg height={radius * 2} width={radius * 2}>
        <circle
          stroke="#e6e6e6"
          fill="transparent"
          strokeWidth={stroke}
          r={normalizedRadius}
          cx={radius}
          cy={radius}
        />
        <circle
          stroke={color}
          fill="transparent"
          strokeWidth={stroke}
          strokeDasharray={circumference + ' ' + circumference}
          style={{ strokeDashoffset, transition: 'stroke-dashoffset 0.5s ease 0s', transform: 'rotate(-90deg)', transformOrigin: '50% 50%' }}
          r={normalizedRadius}
          cx={radius}
          cy={radius}
        />
        <text x="50%" y="50%" textAnchor="middle" dy=".3em" fontSize="1.2em" fontWeight="bold">
          {progress}%
        </text>
      </svg>
    );
  };

  const getFormBadge = (status: string) => {
    switch (status) {
      case 'SUBMITTED':
        return <span className="badge bg-success fs-6">Submitted</span>;
      case 'PENDING':
        return <span className="badge bg-primary fs-6">Pending</span>;
      default:
        return <span className="badge bg-secondary fs-6">None</span>;
    }
  };

  const handleForm90cClick = () => {
    if (summary?.activeFormStatus === 'SUBMITTED') {
      toast.info(`Form 90C already submitted for FY ${financialYear}`);
    } else {
      navigate('/form90c', { state: { financialYear } });
    }
  };

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
      
      {loading ? (
        <div className="text-center my-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      ) : summary !== null ? (
        <>
          {/* Row 1: Hero & Chart */}
          <div className="row g-4 mb-4">
            <div className="col-md-5">
              <div className="card h-100 bg-success text-white rounded-4 p-4 border-0 shadow">
                <div className="card-body d-flex flex-column justify-content-center">
                  <h5 className="card-title fw-light mb-3">Total Tax Saved</h5>
                  <h1 className="display-4 fw-bold mb-3">
                    ₹{summary.totalTaxSaved?.toLocaleString('en-IN') || 0}
                  </h1>
                  <p className="mb-0 text-white-50">
                    <i className={summary.trendPercentageChange >= 0 ? "bi bi-arrow-up-right me-1" : "bi bi-arrow-down-right me-1"}></i>
                    {summary.trendPercentageChange}% vs last month
                  </p>
                </div>
              </div>
            </div>
            <div className="col-md-7">
              <div className="card h-100 shadow-sm border rounded p-3">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <h5 className="mb-0 fw-bold">Tax Savings Trend</h5>
                  <select 
                    className="form-select form-select-sm w-auto" 
                    value={financialYear} 
                    onChange={(e) => setFinancialYear(e.target.value)}
                  >
                    {financialYears.map(year => (
                      <option key={year} value={year}>{year}</option>
                    ))}
                  </select>
                </div>
                <div style={{ height: '200px' }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={summary.taxSavingsTrend}>
                      <CartesianGrid strokeDasharray="3 3" vertical={false} />
                      <XAxis dataKey="month" axisLine={false} tickLine={false} />
                      <YAxis axisLine={false} tickLine={false} tickFormatter={(val) => `₹${val}`} />
                      <Tooltip formatter={(value) => `₹${value}`} />
                      <Line type="monotone" dataKey="amount" stroke="#198754" strokeWidth={3} dot={{ r: 4 }} activeDot={{ r: 6 }} />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>
          </div>

          {/* Row 2: Rings & Status */}
          <div className="row g-4 mb-4">
            <div className="col-md-4">
              <div className="card h-100 shadow-sm border rounded text-center p-3">
                <h6 className="fw-bold text-muted mb-3">TDS Deducted</h6>
                <div className="d-flex justify-content-center align-items-center">
                  <ProgressRing radius={50} stroke={8} progress={summary.tdsPercentage} color="#0d6efd" />
                </div>
                <div className="mt-3 fs-5 fw-bold text-primary">₹{summary.tdsDeducted?.toLocaleString('en-IN') || 0}</div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card h-100 shadow-sm border rounded text-center p-3">
                <h6 className="fw-bold text-muted mb-3">TCS Deducted</h6>
                <div className="d-flex justify-content-center align-items-center">
                  <ProgressRing radius={50} stroke={8} progress={summary.tcsPercentage} color="#fd7e14" />
                </div>
                <div className="mt-3 fs-5 fw-bold text-warning">₹{summary.tcsDeducted?.toLocaleString('en-IN') || 0}</div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="card h-100 shadow-sm border rounded text-center p-4">
                <h6 className="fw-bold text-muted mb-4">Active Form 90C</h6>
                <div className="mb-4">
                  {getFormBadge(summary.activeFormStatus)}
                </div>
                <p className="text-muted small mb-0">Financial Year {financialYear}</p>
              </div>
            </div>
          </div>
        </>
      ) : (
        <div className="alert alert-info" role="alert">
          <i className="bi bi-info-circle me-2"></i>
          No transactions found. Add a transaction to start tracking your tax savings!
        </div>
      )}

      {/* Nav Cards Section */}
      <div className="row g-4 mb-5">
        <div className="col-md-4">
          <div className="card h-100 shadow-sm border rounded" onClick={() => navigate('/transactions')} style={{ cursor: 'pointer' }}>
            <div className="card-body p-4 text-center">
              <div className="mb-3 text-info" style={{ fontSize: '2.5rem' }}>
                <i className="bi bi-plus-circle"></i>
              </div>
              <h5 className="card-title fw-bold">Add Transaction</h5>
              <p className="card-text text-muted mb-3 small">
                Quickly record a new TDS or TCS transaction to keep your records up to date.
              </p>
              <button className="btn btn-outline-info btn-sm w-100">
                Add Transaction <i className="bi bi-arrow-right ms-1"></i>
              </button>
            </div>
          </div>
        </div>

        <div className="col-md-4">
          <div className="card h-100 shadow-sm border rounded" onClick={() => navigate('/transactions')} style={{ cursor: 'pointer' }}>
            <div className="card-body p-4 text-center">
              <div className="mb-3 text-primary" style={{ fontSize: '2.5rem' }}>
                <i className="bi bi-receipt-cutoff"></i>
              </div>
              <h5 className="card-title fw-bold">Transactions</h5>
              <p className="card-text text-muted mb-3 small">
                View, filter, and download your yearly transactions in JSON or CSV format.
              </p>
              <button className="btn btn-outline-primary btn-sm w-100">
                View Transactions <i className="bi bi-arrow-right ms-1"></i>
              </button>
            </div>
          </div>
        </div>
        
        <div className="col-md-4">
          <div className="card h-100 shadow-sm border rounded" onClick={handleForm90cClick} style={{ cursor: 'pointer' }}>
            <div className="card-body p-4 text-center">
              <div className="mb-3 text-success" style={{ fontSize: '2.5rem' }}>
                <i className="bi bi-file-earmark-check"></i>
              </div>
              <h5 className="card-title fw-bold">Form 90C</h5>
              <p className="card-text text-muted mb-3 small">
                File your Form 90C based on your transactions. Save drafts and upload documents securely.
              </p>
              <button className="btn btn-outline-success btn-sm w-100">
                {summary?.activeFormStatus === 'PENDING' ? 'Continue Form 90C' : 'Start Form 90C'} <i className="bi bi-arrow-right ms-1"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
