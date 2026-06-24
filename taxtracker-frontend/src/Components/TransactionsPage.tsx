import React, { useState, useEffect, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { transactionApi } from '../api/transactionApi';
import { toast } from 'react-toastify';

export const TransactionsPage = () => {
  const [transactions, setTransactions] = useState<any[]>([]);
  const [totalRecords, setTotalRecords] = useState(0);
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState<any>({});
  
  // For Add Transaction form
  const { register: regAdd, handleSubmit: handleAddSubmit, reset: resetAdd, formState: { errors: addErrors } } = useForm();
  const [downloadFormat, setDownloadFormat] = useState('JSON');

  const fetchTransactions = useCallback(async () => {
    try {
      const data = await transactionApi.getTransactions({ ...filters, pageNumber: page, pageSize: 10 });
      setTransactions(data.content || []);
      setTotalRecords(data.totalElements || 0);
    } catch (error) {
      console.error('Failed to load transactions');
    }
  }, [filters, page]);

  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  const onAddSubmit = async (data: any) => {
    try {
      const payload = {
        ...data,
        amount: parseFloat(data.amount),
        taxAmount: parseFloat(data.taxAmount)
      };
      await transactionApi.addTransaction(payload);
      toast.success('Transaction added successfully!');
      resetAdd();
      fetchTransactions();
    } catch (error: any) {
      if (error.response?.data?.fieldErrors) {
         toast.error(error.response.data.fieldErrors.map((f: any) => f.message).join(', '));
      } else {
         toast.error(error.response?.data?.message || 'Failed to add transaction.');
      }
    }
  };

  const handleFilterChange = (e: any) => {
    const { name, value } = e.target;
    setFilters((prev: any) => ({ ...prev, [name]: value || undefined }));
    setPage(0); // reset to first page on filter change
  };

  const downloadData = async () => {
    try {
      const blob = await transactionApi.downloadTransactions(filters, downloadFormat);
      const url = window.URL.createObjectURL(new Blob([blob]));
      const a = document.createElement('a');
      a.href = url;
      a.setAttribute('download', `transactions.${downloadFormat.toLowerCase()}`);
      document.body.appendChild(a);
      a.click();
      a.parentNode?.removeChild(a);
    } catch (error: any) {
      toast.error('Failed to download: ' + (error.response?.data?.message || error.message || 'Unknown error'));
    }
  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Your Transactions</h2>
        <div className="d-flex gap-2">
          <select className="form-select" value={downloadFormat} onChange={(e) => setDownloadFormat(e.target.value)}>
            <option value="JSON">JSON</option>
            <option value="CSV">CSV</option>
          </select>
          <button className="btn btn-outline-primary" onClick={downloadData}>Download</button>
        </div>
      </div>

      {/* Add Transaction Section (For Capstone Testing) */}
      <div className="card mb-4 shadow-sm">
        <div className="card-body">
          <h5 className="card-title">Add Sample Transaction</h5>
          <form onSubmit={handleAddSubmit(onAddSubmit)} className="row g-3">
            <div className="col-md-3">
              <input type="date" className="form-control" {...regAdd('transactionDate', { required: true })} />
            </div>
            <div className="col-md-2">
              <input type="number" step="0.01" className="form-control" placeholder="Amount" {...regAdd('amount', { required: true })} />
            </div>
            <div className="col-md-2">
              <input type="number" step="0.01" className="form-control" placeholder="Tax Amount" {...regAdd('taxAmount', { required: true })} />
            </div>
            <div className="col-md-2">
              <select className="form-select" {...regAdd('type', { required: true })}>
                <option value="">Select Type</option>
                <option value="TDS">TDS</option>
                <option value="TCS">TCS</option>
                <option value="OTHER">OTHER</option>
              </select>
            </div>
            <div className="col-md-3">
              <input type="text" className="form-control" placeholder="Organization" {...regAdd('organizationName', { required: true })} />
            </div>
            <div className="col-12">
              <button type="submit" className="btn btn-sm btn-success">Add Transaction</button>
            </div>
          </form>
        </div>
      </div>

      {/* Filters Section */}
      <div className="card mb-4 shadow-sm bg-light">
        <div className="card-body row g-3">
          <div className="col-md-3">
            <label className="form-label">Financial Year</label>
            <select className="form-select" name="financialYear" onChange={handleFilterChange} value={filters.financialYear || ''}>
              <option value="">All</option>
              <option value="2022-2023">2022-2023</option>
              <option value="2023-2024">2023-2024</option>
              <option value="2024-2025">2024-2025</option>
            </select>
          </div>
          <div className="col-md-2">
            <label className="form-label">Month</label>
            <select className="form-select" name="month" onChange={handleFilterChange} value={filters.month || ''}>
              <option value="">All</option>
              <option value="1">Jan (01)</option>
              <option value="2">Feb (02)</option>
              <option value="3">Mar (03)</option>
              <option value="4">Apr (04)</option>
              <option value="5">May (05)</option>
              <option value="6">Jun (06)</option>
              <option value="7">Jul (07)</option>
              <option value="8">Aug (08)</option>
              <option value="9">Sep (09)</option>
              <option value="10">Oct (10)</option>
              <option value="11">Nov (11)</option>
              <option value="12">Dec (12)</option>
            </select>
          </div>
          <div className="col-md-3">
            <label className="form-label">Type</label>
            <input type="text" className="form-control" name="type" placeholder="e.g. TDS" onChange={handleFilterChange} value={filters.type || ''}/>
          </div>
          <div className="col-md-4">
            <label className="form-label">Organization</label>
            <input type="text" className="form-control" name="organizationName" placeholder="Search org..." onChange={handleFilterChange} value={filters.organizationName || ''}/>
          </div>
        </div>
      </div>

      {/* Table Section */}
      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead className="table-dark">
            <tr>
              <th>Date</th>
              <th>Org Name</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Tax Amount</th>
              <th>Fin. Year</th>
            </tr>
          </thead>
          <tbody>
            {transactions.length === 0 ? (
              <tr><td colSpan={6} className="text-center">No transactions found.</td></tr>
            ) : (
              transactions.map((txn: any) => (
                <tr key={txn.id}>
                  <td>{txn.transactionDate}</td>
                  <td>{txn.organizationName}</td>
                  <td>{txn.type}</td>
                  <td>₹{txn.amount.toFixed(2)}</td>
                  <td>₹{txn.taxAmount.toFixed(2)}</td>
                  <td>{txn.financialYear}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      <div className="d-flex justify-content-between align-items-center">
        <span>Total Records: {totalRecords}</span>
        <div>
          <button className="btn btn-sm btn-outline-secondary me-2" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span>Page {page + 1}</span>
          <button className="btn btn-sm btn-outline-secondary ms-2" disabled={transactions.length < 10} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      </div>
    </div>
  );
};
