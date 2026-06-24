import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/authApi';

export const RegisterPage = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [errorMsg, setErrorMsg] = useState('');

  const onSubmit = async (data: any) => {
    try {
      await authApi.register(data);
      navigate('/login');
    } catch (error: any) {
      if (error.response?.data?.message) {
        setErrorMsg(error.response.data.message);
      } else {
        setErrorMsg('Registration failed.');
      }
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card shadow">
            <div className="card-body">
              <h3 className="card-title text-center mb-4">Register for TaxTracker</h3>
              {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}
              
              <form onSubmit={handleSubmit(onSubmit)}>
                <div className="row">
                  <div className="col-md-4 mb-3">
                    <label className="form-label">First Name</label>
                    <input type="text" className={`form-control ${errors.firstName ? 'is-invalid' : ''}`}
                      {...register('firstName', { required: 'Please provide a valid First Name' })} />
                    {errors.firstName && <div className="invalid-feedback">{errors.firstName.message as string}</div>}
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Middle Name (Optional)</label>
                    <input type="text" className="form-control" {...register('middleName')} />
                  </div>
                  <div className="col-md-4 mb-3">
                    <label className="form-label">Last Name</label>
                    <input type="text" className={`form-control ${errors.lastName ? 'is-invalid' : ''}`}
                      {...register('lastName', { required: 'Please provide a valid Last Name' })} />
                    {errors.lastName && <div className="invalid-feedback">{errors.lastName.message as string}</div>}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Email</label>
                    <input type="email" className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                      {...register('email', { required: 'Please provide a valid Email' })} />
                    {errors.email && <div className="invalid-feedback">{errors.email.message as string}</div>}
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Mobile Number</label>
                    <input type="text" className={`form-control ${errors.mobileNumber ? 'is-invalid' : ''}`}
                      {...register('mobileNumber', { required: 'Please provide a valid Mobile Number' })} />
                    {errors.mobileNumber && <div className="invalid-feedback">{errors.mobileNumber.message as string}</div>}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Password</label>
                    <input type="password" className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                      {...register('password', { required: 'Please provide a valid Password' })} />
                    {errors.password && <div className="invalid-feedback">{errors.password.message as string}</div>}
                  </div>
                </div>

                <hr/>
                <h5 className="mb-3">Address</h5>
                
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Address Line 1</label>
                    <input type="text" className={`form-control ${errors.addressLine1 ? 'is-invalid' : ''}`}
                      {...register('addressLine1', { required: 'Please provide a valid Address Line 1' })} />
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Address Line 2</label>
                    <input type="text" className={`form-control ${errors.addressLine2 ? 'is-invalid' : ''}`}
                      {...register('addressLine2', { required: 'Please provide a valid Address Line 2' })} />
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-3 mb-3">
                    <label className="form-label">Area</label>
                    <input type="text" className={`form-control ${errors.area ? 'is-invalid' : ''}`}
                      {...register('area', { required: 'Please provide a valid Area' })} />
                  </div>
                  <div className="col-md-3 mb-3">
                    <label className="form-label">City</label>
                    <input type="text" className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                      {...register('city', { required: 'Please provide a valid City' })} />
                  </div>
                  <div className="col-md-3 mb-3">
                    <label className="form-label">State</label>
                    <input type="text" className={`form-control ${errors.state ? 'is-invalid' : ''}`}
                      {...register('state', { required: 'Please provide a valid State' })} />
                  </div>
                  <div className="col-md-3 mb-3">
                    <label className="form-label">PIN Code</label>
                    <input type="text" className={`form-control ${errors.pincode ? 'is-invalid' : ''}`}
                      {...register('pincode', { required: 'Please provide a valid PIN Code' })} />
                  </div>
                </div>

                <div className="d-grid gap-2">
                  <button type="submit" className="btn btn-primary">Register</button>
                </div>
                
                <div className="text-center mt-3">
                  <Link to="/login">Already have an account? Log in</Link>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
