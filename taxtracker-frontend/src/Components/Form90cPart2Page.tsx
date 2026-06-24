import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { formApi } from '../api/formApi';
import { toast } from 'react-toastify';

export const Form90cPart2Page = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const formId = location.state?.formId;

  const [files, setFiles] = useState<any[]>([]);
  const [submitting, setSubmitting] = useState(false);

  if (!formId) {
    return <div className="container mt-5">Error: No Form ID found. Please complete Part 1 first.</div>;
  }

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate size (max 2MB)
    if (file.size > 2 * 1024 * 1024) {
      toast.error("File size exceeds 2MB limit.");
      e.target.value = ''; // Reset input
      return;
    }

    // Validate type (PDF, JPG, JPEG)
    const validTypes = ['application/pdf', 'image/jpeg', 'image/jpg'];
    if (!validTypes.includes(file.type)) {
      toast.error("Invalid file format. Only PDF and JPG are allowed.");
      e.target.value = ''; // Reset input
      return;
    }

    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = async () => {
      const base64Data = (reader.result as string).split(',')[1];
      
      try {
        await formApi.uploadDocument({
          formId,
          name: file.name,
          data: base64Data
        });
        setFiles([...files, file.name]);
        toast.success('File uploaded successfully.');
      } catch (err: any) {
        toast.error(err.response?.data?.message || 'Upload failed');
      } finally {
        e.target.value = ''; // Reset input
      }
    };
  };

  const handleRemove = (name: string) => {
    setFiles(files.filter(f => f !== name));
  };

  const handleSubmit = async () => {
    try {
      if (files.length === 0) {
        toast.error("Please upload at least one supporting document.");
        return;
      }
      
      setSubmitting(true);
      const response = await formApi.submitForm(formId);
      toast.success(response.message || "Form 90C submitted successfully!");
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Submission failed');
      setSubmitting(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="card shadow-sm border-0 bg-light">
        <div className="card-body p-5">
          <h2 className="mb-4" style={{ color: "#6F42C1" }}>Form 90C - Part 2 (Upload Documents)</h2>
          
          <div className="mb-4">
            <label className="form-label fw-bold">Upload Document (PDF or JPG, Max: 2MB)</label>
            <input 
              type="file" 
              className="form-control form-control-lg" 
              accept=".pdf,.jpg,.jpeg" 
              onChange={handleFileUpload} 
            />
          </div>

          <ul className="list-group mb-5">
            {files.map((file, idx) => (
              <li key={idx} className="list-group-item d-flex justify-content-between align-items-center">
                <span><i className="bi bi-file-earmark-text me-2"></i>{file}</span>
                <button className="btn btn-sm btn-outline-danger" onClick={() => handleRemove(file)}>
                  <i className="bi bi-trash"></i> Remove
                </button>
              </li>
            ))}
          </ul>

          <div className="d-grid">
            <button className="btn btn-lg btn-success shadow-sm" onClick={handleSubmit} disabled={submitting}>
              <i className="bi bi-check-circle me-2"></i>{submitting ? "Submitting..." : "Submit Final Form"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
