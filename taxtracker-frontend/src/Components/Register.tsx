import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { authApi } from "../api/authApi";
import { toast } from "react-toastify";

const Register = () => {
  const { register, handleSubmit, formState: { errors } } = useForm();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onSubmit = async (data: any) => {
    try {
      setLoading(true);
      await authApi.register(data);
      toast.success("Registration successful! Please login.");
      navigate("/login");
    } catch (error: any) {
      toast.error(error.response?.data?.message || "Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-8">
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
                Register
              </h2>

              <p className="text-center text-muted mb-4">
                Create your Tax Tracker account
              </p>

              <form onSubmit={handleSubmit(onSubmit)}>
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Name</label>
                    <input
                      type="text"
                      className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                      placeholder="Enter your name"
                      {...register("name", { required: "Name is required" })}
                    />
                    {errors.name && <div className="invalid-feedback">{errors.name.message as string}</div>}
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">Email</label>
                    <input
                      type="email"
                      className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                      placeholder="Enter your email"
                      {...register("email", { required: "Email is required" })}
                    />
                    {errors.email && <div className="invalid-feedback">{errors.email.message as string}</div>}
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Password</label>
                    <input
                      type="password"
                      className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                      placeholder="Enter password"
                      {...register("password", { required: "Password is required" })}
                    />
                    {errors.password && <div className="invalid-feedback">{errors.password.message as string}</div>}
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">Mobile Number</label>
                    <input
                      type="tel"
                      className={`form-control ${errors.mobileNumber ? 'is-invalid' : ''}`}
                      placeholder="Enter mobile number"
                      {...register("mobileNumber", { required: "Mobile Number is required" })}
                    />
                    {errors.mobileNumber && <div className="invalid-feedback">{errors.mobileNumber.message as string}</div>}
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Address Line 1</label>
                  <input
                    type="text"
                    className={`form-control ${errors.addressLine1 ? 'is-invalid' : ''}`}
                    placeholder="Flat / House Number"
                    {...register("addressLine1", { required: "Address Line 1 is required" })}
                  />
                  {errors.addressLine1 && <div className="invalid-feedback">{errors.addressLine1.message as string}</div>}
                </div>

                <div className="mb-3">
                  <label className="form-label">Address Line 2</label>
                  <input
                    type="text"
                    className={`form-control ${errors.addressLine2 ? 'is-invalid' : ''}`}
                    placeholder="Street Name"
                    {...register("addressLine2", { required: "Address Line 2 is required" })}
                  />
                  {errors.addressLine2 && <div className="invalid-feedback">{errors.addressLine2.message as string}</div>}
                </div>

                <div className="mb-3">
                  <label className="form-label">Area</label>
                  <input
                    type="text"
                    className={`form-control ${errors.area ? 'is-invalid' : ''}`}
                    placeholder="Area"
                    {...register("area", { required: "Area is required" })}
                  />
                  {errors.area && <div className="invalid-feedback">{errors.area.message as string}</div>}
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">City</label>
                    <input
                      type="text"
                      className={`form-control ${errors.city ? 'is-invalid' : ''}`}
                      placeholder="City"
                      {...register("city", { required: "City is required" })}
                    />
                    {errors.city && <div className="invalid-feedback">{errors.city.message as string}</div>}
                  </div>

                  <div className="col-md-6 mb-3">
                    <label className="form-label">State</label>
                    <select
                      className={`form-select ${errors.state ? 'is-invalid' : ''}`}
                      {...register("state", { required: "State is required" })}
                    >
                      <option value="">Select State</option>
                      <option value="Andhra Pradesh">Andhra Pradesh</option>
                      <option value="Arunachal Pradesh">Arunachal Pradesh</option>
                      <option value="Assam">Assam</option>
                      <option value="Bihar">Bihar</option>
                      <option value="Chhattisgarh">Chhattisgarh</option>
                      <option value="Goa">Goa</option>
                      <option value="Gujarat">Gujarat</option>
                      <option value="Haryana">Haryana</option>
                      <option value="Himachal Pradesh">Himachal Pradesh</option>
                      <option value="Jharkhand">Jharkhand</option>
                      <option value="Karnataka">Karnataka</option>
                      <option value="Kerala">Kerala</option>
                      <option value="Madhya Pradesh">Madhya Pradesh</option>
                      <option value="Maharashtra">Maharashtra</option>
                      <option value="Manipur">Manipur</option>
                      <option value="Meghalaya">Meghalaya</option>
                      <option value="Mizoram">Mizoram</option>
                      <option value="Nagaland">Nagaland</option>
                      <option value="Odisha">Odisha</option>
                      <option value="Punjab">Punjab</option>
                      <option value="Rajasthan">Rajasthan</option>
                      <option value="Sikkim">Sikkim</option>
                      <option value="Tamil Nadu">Tamil Nadu</option>
                      <option value="Telangana">Telangana</option>
                      <option value="Tripura">Tripura</option>
                      <option value="Uttar Pradesh">Uttar Pradesh</option>
                      <option value="Uttarakhand">Uttarakhand</option>
                      <option value="West Bengal">West Bengal</option>
                    </select>
                    {errors.state && <div className="invalid-feedback">{errors.state.message as string}</div>}
                  </div>
                </div>

                <div className="mb-4">
                  <label className="form-label">PIN Code</label>
                  <input
                    type="text"
                    className={`form-control ${errors.pincode ? 'is-invalid' : ''}`}
                    placeholder="PIN Code"
                    {...register("pincode", { required: "PIN Code is required" })}
                  />
                  {errors.pincode && <div className="invalid-feedback">{errors.pincode.message as string}</div>}
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
                    {loading ? "Registering..." : "Register"}
                  </button>
                </div>
              </form>

              <p className="text-center mt-4 mb-0">
                Already have an account?{" "}
                <Link
                  to="/login"
                  style={{
                    color: "#6F42C1",
                    textDecoration: "none",
                  }}
                >
                  Login
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
