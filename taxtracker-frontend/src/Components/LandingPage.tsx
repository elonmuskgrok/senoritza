import React from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";

const LandingPage = () => {
  const handleFeatureClick = () => {
    toast.info("Please login to access this feature.", {
      position: "top-right",
      autoClose: 3000,
    });
  };

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      {/* Hero Section */}
      <div className="bg-white border-bottom pt-5 pb-5">
        <div className="container py-5 text-center">
          <h1 className="display-4 fw-bold mb-4" style={{ color: "#2d3748" }}>
            Smart Tax Management <span style={{ color: "#6F42C1" }}>Made Simple</span>
          </h1>
          <p className="lead text-muted mb-5 mx-auto" style={{ maxWidth: "700px" }}>
            Securely store your transactions, download customized reports, and seamlessly submit your Form 90C declarations all in one highly available platform.
          </p>
          <div className="d-flex justify-content-center gap-3">
            <Link to="/register" className="btn btn-primary btn-lg px-5 shadow-sm fw-bold" style={{ backgroundColor: "#6F42C1", borderColor: "#6F42C1" }}>
              Get Started Now <i className="bi bi-arrow-right ms-2"></i>
            </Link>
            <Link to="/login" className="btn btn-outline-secondary btn-lg px-5 fw-bold">
              Sign In
            </Link>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="container py-5 flex-grow-1">
        <div className="text-center mb-5">
          <h2 className="fw-bold" style={{ color: "#2d3748" }}>Powerful Features</h2>
          <p className="text-muted">Everything you need to manage your tax workflow.</p>
        </div>

        <div className="row g-4">
          {/* Feature 1 */}
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
              <div className="card-body">
                <div className="mb-4 d-inline-flex align-items-center justify-content-center bg-light rounded-circle" style={{ width: "80px", height: "80px" }}>
                  <i className="bi bi-credit-card" style={{ fontSize: "2.5rem", color: "#6F42C1" }}></i>
                </div>
                <h4 className="fw-bold mb-3">Store Transactions</h4>
                <p className="text-muted mb-0">Securely store and manage all your transaction history in one centralized database.</p>
              </div>
            </div>
          </div>

          {/* Feature 2 */}
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
              <div className="card-body">
                <div className="mb-4 d-inline-flex align-items-center justify-content-center bg-light rounded-circle" style={{ width: "80px", height: "80px" }}>
                  <i className="bi bi-file-earmark-arrow-down" style={{ fontSize: "2.5rem", color: "#0d6efd" }}></i>
                </div>
                <h4 className="fw-bold mb-3">View & Download</h4>
                <p className="text-muted mb-0">Quickly filter your transactions and download comprehensive reports in JSON or CSV formats.</p>
              </div>
            </div>
          </div>

          {/* Feature 3 */}
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
              <div className="card-body">
                <div className="mb-4 d-inline-flex align-items-center justify-content-center bg-light rounded-circle" style={{ width: "80px", height: "80px" }}>
                  <i className="bi bi-file-earmark-check" style={{ fontSize: "2.5rem", color: "#198754" }}></i>
                </div>
                <h4 className="fw-bold mb-3">Submit Form 90C</h4>
                <p className="text-muted mb-0">Easily file tax-saving declarations, save your progress as a draft, and submit when ready.</p>
              </div>
            </div>
          </div>

          {/* Feature 4 */}
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
              <div className="card-body">
                <div className="mb-4 d-inline-flex align-items-center justify-content-center bg-light rounded-circle" style={{ width: "80px", height: "80px" }}>
                  <i className="bi bi-folder2-open" style={{ fontSize: "2.5rem", color: "#fd7e14" }}></i>
                </div>
                <h4 className="fw-bold mb-3">Upload Documents</h4>
                <p className="text-muted mb-0">Attach supporting PDF and JPG documents securely alongside your Form 90C filings.</p>
              </div>
            </div>
          </div>

          {/* Feature 5 */}
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
              <div className="card-body">
                <div className="mb-4 d-inline-flex align-items-center justify-content-center bg-light rounded-circle" style={{ width: "80px", height: "80px" }}>
                  <i className="bi bi-shield-lock" style={{ fontSize: "2.5rem", color: "#dc3545" }}></i>
                </div>
                <h4 className="fw-bold mb-3">Bank-Grade Security</h4>
                <p className="text-muted mb-0">Your financial data is protected with industry-standard JWT authentication and strict access controls.</p>
              </div>
            </div>
          </div>

          {/* Feature 6 */}
          <div className="col-md-4">
            <div className="card h-100 shadow-sm border-0 rounded-4 text-center p-4">
              <div className="card-body">
                <div className="mb-4 d-inline-flex align-items-center justify-content-center bg-light rounded-circle" style={{ width: "80px", height: "80px" }}>
                  <i className="bi bi-cloud-check" style={{ fontSize: "2.5rem", color: "#0dcaf0" }}></i>
                </div>
                <h4 className="fw-bold mb-3">Highly Available</h4>
                <p className="text-muted mb-0">Experience a fast, hassle-free tax management workflow with zero downtime architecture.</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-white border-top py-4 mt-auto">
        <div className="container text-center">
          <p className="text-muted mb-0 fw-semibold">&copy; 2026 TaxTracker. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
