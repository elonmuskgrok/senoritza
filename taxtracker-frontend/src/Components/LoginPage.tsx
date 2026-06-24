import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/authApi';

export const LoginPage = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [errorMsg, setErrorMsg] = useState('');

  const onSubmit = async (data: any) => {
    try {
      const response = await authApi.login(data);
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(response));
      navigate('/dashboard');
    } catch (error: any) {
      if (error.response?.data?.message) {
        setErrorMsg(error.response.data.message);
      } else {
        setErrorMsg('The email address or password is incorrect.');
      }
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow">
            <div className="card-body">
              <h3 className="card-title text-center mb-4">Log In to TaxTracker</h3>
              {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}
              
              <form onSubmit={handleSubmit(onSubmit)}>
                <div className="mb-3">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                    {...register('email', { required: 'Please provide a valid Email' })}
                  />
                  {errors.email && <div className="invalid-feedback">{errors.email.message as string}</div>}
                </div>

                <div className="mb-3">
                  <label className="form-label">Password</label>
                  <input
                    type="password"
                    className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                    {...register('password', { required: 'Please provide a valid Password' })}
                  />
                  {errors.password && <div className="invalid-feedback">{errors.password.message as string}</div>}
                </div>

                <div className="d-grid gap-2">
                  <button type="submit" className="btn btn-primary">Log In</button>
                </div>
                
                <div className="text-center mt-3">
                  <Link to="/register">Create an account</Link>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
