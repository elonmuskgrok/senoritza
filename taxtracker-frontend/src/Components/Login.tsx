import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { authApi } from "../api/authApi";
import { toast } from "react-toastify";

const Login = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onSubmit = async (data: any) => {
    try {
      setLoading(true);
      const response = await authApi.login(data);
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(response));
      toast.success("Login successful!");
      navigate('/dashboard');
    } catch (error: any) {
      toast.error(error.response?.data?.message || "Invalid email or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-5">
          <div
            className="card border-0 shadow"
            style={{
              backgroundColor: "#f9fafaff",
            }}
          >
            <div className="card-body p-5">
              <h2
                className="text-center fw-bold mb-2"
                style={{ color: "#6F42C1" }}
              >
                Login
              </h2>

              <h4 className="text-center text-muted mb-4">
                Welcome Back ['-']
              </h4>

              <form onSubmit={handleSubmit(onSubmit)}>
                <div className="mb-3">
                  <label className="form-label">Email Address</label>

                  <input
                    type="email"
                    className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                    placeholder="Enter your email"
                    {...register("email", { required: "Email is required" })}
                  />
                  {errors.email && <div className="invalid-feedback">{errors.email.message as string}</div>}
                </div>

                <div className="mb-3">
                  <label className="form-label">Password</label>

                  <input
                    type="password"
                    className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                    placeholder="Enter your password"
                    {...register("password", { required: "Password is required" })}
                  />
                  {errors.password && <div className="invalid-feedback">{errors.password.message as string}</div>}
                </div>

                <div className="text-end mb-4">
                  <Link
                    to="/forgot-password"
                    style={{
                      color: "#6F42C1",
                      textDecoration: "none",
                    }}
                  >
                    Forgot Password?
                  </Link>
                </div>

                <div className="d-grid">
                  <button
                    type="submit"
                    className="btn btn-lg"
                    disabled={loading}
                    style={{
                      backgroundColor: "#6F42C1",
                      color: "white",
                    }}
                  >
                    {loading ? "Logging in..." : "Login"}
                  </button>
                </div>
              </form>

              <p className="text-center mt-4 mb-0">
                Don't have an account?{" "}
                <Link
                  to="/register"
                  style={{
                    color: "#6F42C1",
                    textDecoration: "none",
                  }}
                >
                  Register
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
