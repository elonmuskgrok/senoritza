import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { formApi } from '../api/formApi';
import { transactionApi } from '../api/transactionApi';
import { toast } from 'react-toastify';

export const Form90cPart1Page = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [errorMsg, setErrorMsg] = useState('');
  const hasLoadedDraft = useRef(false);
  
  const [financialYears, setFinancialYears] = useState<string[]>([]);
  const [selectedFY, setSelectedFY] = useState<string>(location.state?.financialYear || '');

  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const email = user.email;

  const { register, control, handleSubmit, reset, getValues, setValue, formState: { errors } } = useForm({
    defaultValues: {
      name: '',
      mobileNumber: '',
      financialYear: location.state?.financialYear || '',
      transactionHistory: [{ organizationName: '', amount: '', taxAmount: '0', type: 'TDS' }]
    }
  });
  
  const { fields, append, remove } = useFieldArray({
    control,
    name: "transactionHistory"
  });

  useEffect(() => {
    const fetchYears = async () => {
      try {
        const years = await transactionApi.getAvailableFinancialYears();
        let finalYears = [...years];
        
        const today = new Date();
        const month = today.getMonth() + 1;
        const year = today.getFullYear();
        const currentFY = month >= 4 ? `${year}-${year + 1}` : `${year - 1}-${year}`;
        
        if (!finalYears.includes(currentFY)) {
          finalYears.unshift(currentFY);
        }
        
        setFinancialYears(finalYears);
        
        if (!selectedFY && finalYears.length > 0) {
          setSelectedFY(finalYears[0]);
          setValue('financialYear', finalYears[0]);
        }
      } catch (e) {
        console.error("Failed to load years", e);
      }
    };
    fetchYears();
  }, [selectedFY, setValue]);

  useEffect(() => {
    const fetchDraft = async () => {
      if (!email || !selectedFY) return;
      try {
        const draft = await formApi.getForm(selectedFY);
        if (draft && draft.status === 'DRAFT') {
          reset({
            name: draft.name || '',
            mobileNumber: draft.mobileNumber || '',
            financialYear: selectedFY,
            transactionHistory: draft.transactionHistory && draft.transactionHistory.length > 0 
              ? draft.transactionHistory 
              : [{ organizationName: '', amount: '', taxAmount: '0', type: 'TDS' }]
          });
          if (!hasLoadedDraft.current) {
            toast.info(`Loaded your saved draft for FY ${selectedFY}!`);
            hasLoadedDraft.current = true;
          }
        }
      } catch (err: any) {
        reset({
          name: getValues('name'), 
          mobileNumber: getValues('mobileNumber'),
          financialYear: selectedFY,
          transactionHistory: [{ organizationName: '', amount: '', taxAmount: '0', type: 'TDS' }]
        });
      }
    };
    fetchDraft();
  }, [email, selectedFY, reset, getValues]);

  const saveForm = async (data: any, navigateToNext: boolean) => {
    try {
      const formattedData = {
        ...data,
        transactionHistory: data.transactionHistory.map((t: any) => ({
          ...t,
          amount: parseFloat(t.amount) || 0,
          taxAmount: parseFloat(t.taxAmount) || 0
        }))
      };
      
      let response;
      if (navigateToNext) {
        response = await formApi.saveForm(formattedData);
        navigate('/form90c/upload', { state: { formId: response.formId } });
      } else {
        response = await formApi.saveDraft(formattedData);
        toast.success("Draft saved successfully!");
      }
    } catch (err: any) {
      if (err.response?.data?.fieldErrors) {
        setErrorMsg(err.response.data.fieldErrors.map((f: any) => f.message).join(', '));
      } else {
        setErrorMsg(err.response?.data?.message || 'Failed to save form');
      }
      toast.error(navigateToNext ? "Failed to save and continue" : "Failed to save draft");
    }
  };

  const onSaveDraft = () => {
    const data = getValues();
    saveForm(data, false);
  };
  const onSaveAndNext = (data: any) => saveForm(data, true);

  return (
    <div className="container mt-5">
      <div className="mb-4">
        <h2 className="fw-bold">Form 90C - Part 1</h2>
        <p className="text-muted">Fill out your details and review your transactions.</p>
      </div>
      {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}
      
      <form>
        <div className="row mb-3">
          <div className="col-md-4">
            <label className="form-label">Name</label>
            <input 
              className={`form-control ${errors.name ? 'is-invalid' : ''}`} 
              {...register('name', { required: "Name is required" })} 
            />
            {errors.name && <div className="invalid-feedback">{errors.name.message as string}</div>}
          </div>
          <div className="col-md-4">
            <label className="form-label">Mobile Number</label>
            <input 
              className={`form-control ${errors.mobileNumber ? 'is-invalid' : ''}`} 
              {...register('mobileNumber', { required: "Mobile number is required" })} 
            />
            {errors.mobileNumber && <div className="invalid-feedback">{errors.mobileNumber.message as string}</div>}
          </div>
          <div className="col-md-4">
            <label className="form-label">Financial Year</label>
            <select
              className={`form-select ${errors.financialYear ? 'is-invalid' : ''}`}
              {...register('financialYear', { 
                required: "Financial year is required",
                onChange: (e) => {
                  setSelectedFY(e.target.value);
                  hasLoadedDraft.current = false;
                }
              })}
            >
              {financialYears.map(year => (
                <option key={year} value={year}>{year}</option>
              ))}
            </select>
            {errors.financialYear && <div className="invalid-feedback">{errors.financialYear.message as string}</div>}
          </div>
        </div>

        <h4>Transaction History</h4>
        <div className="table-responsive">
          <table className="table table-bordered">
            <thead className="table-light">
              <tr>
                <th>Organization Name</th>
                <th>Amount</th>
                <th>Tax Amount</th>
                <th>Type</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {fields.map((item, index) => (
                <tr key={item.id}>
                  <td>
                    <input 
                      className={`form-control ${errors.transactionHistory?.[index]?.organizationName ? 'is-invalid' : ''}`} 
                      {...register(`transactionHistory.${index}.organizationName` as const, { required: "Required" })} 
                    />
                  </td>
                  <td>
                    <input 
                      type="number" 
                      step="0.01" 
                      className={`form-control ${errors.transactionHistory?.[index]?.amount ? 'is-invalid' : ''}`} 
                      {...register(`transactionHistory.${index}.amount` as const, { required: "Required", min: 0 })} 
                    />
                  </td>
                  <td>
                    <input 
                      type="number" 
                      step="0.01" 
                      className="form-control" 
                      {...register(`transactionHistory.${index}.taxAmount` as const)} 
                    />
                  </td>
                  <td>
                    <select className="form-select" {...register(`transactionHistory.${index}.type` as const)}>
                      <option value="TDS">TDS</option>
                      <option value="TCS">TCS</option>
                      <option value="OTHER">OTHER</option>
                    </select>
                  </td>
                  <td className="text-center">
                    <button type="button" className="btn btn-outline-danger btn-sm" onClick={() => remove(index)}>
                      <i className="bi bi-trash"></i> Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        <div className="mb-4">
          <button type="button" className="btn btn-outline-secondary" onClick={() => append({ organizationName: '', amount: '', taxAmount: '0', type: 'TDS' })}>
            <i className="bi bi-plus-circle"></i> Add Row
          </button>
        </div>

        <div className="d-flex gap-2">
          <button type="button" className="btn btn-secondary" onClick={onSaveDraft}>Save Draft</button>
          <button type="button" className="btn btn-primary" onClick={handleSubmit(onSaveAndNext)}>Save & Continue</button>
        </div>
      </form>
    </div>
  );
};

