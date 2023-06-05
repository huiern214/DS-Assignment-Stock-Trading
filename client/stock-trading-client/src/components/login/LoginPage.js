import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LoginPage.css';
import loginImage from './stock-icon.png';
import success from './success.png';

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmedPassword, setConfirmedPassword] = useState('');
  const [username, setUsername] = useState('');
  const [isSignUp, setIsSignUp] = useState(false); // New state variable
  const [passwordMatch, setPasswordMatch] = useState(true); // New state variable for password match validation
  const [showModal, setShowModal] = useState(false); // New state variable for modal visibility

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
    // Check if passwords match
    setPasswordMatch(e.target.value === confirmedPassword);
  };

  const handleConfirmedPasswordChange = (e) => {
    setConfirmedPassword(e.target.value);
    // Check if passwords match
    setPasswordMatch(e.target.value === password);
  };

  const handleUsernameChange = (e) => {
    setUsername(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Handle login or sign-up logic here
    if (isSignUp) {
        if (!passwordMatch) {
          return; // Disable form submission if passwords don't match
        }
        setShowModal(true); // Show modal on successful sign up
      } else {
        navigate('/', { replace: true }); // Redirect to home page on login
      }
  };

  const handleSignUpClick = () => {
    setEmail('');
    setPassword('');
    setConfirmedPassword('');
    setUsername('');
    setIsSignUp(true);
    setPasswordMatch(true); // Reset password match validation
  };

  const handleSignInClick = () => {
    setEmail('');
    setPassword('');
    setConfirmedPassword('');
    setUsername('');
    setIsSignUp(false);
    setPasswordMatch(true); // Reset password match validation
  };

  const handleModalClose = () => {
    setShowModal(false);
    handleSignInClick(); // Reset form to sign-in mode
  };

  return (
    <div className="login-page">
      <div className="side-page">
        <div className="title">
          <h1 className="welcome-text">Stock Trading Website</h1>
        </div>
        <div className="login-image">
          <img src={loginImage} alt="Login" />
        </div>
      </div>
      <div className="login-form">
        <h2>{isSignUp ? 'Sign Up' : 'Login'}</h2>
        <form onSubmit={handleSubmit}>
          {isSignUp && ( // Render additional fields for sign-up mode
            <div className="form-group">
              <label className="text">Username</label>
              <input
                type="text"
                value={username}
                onChange={handleUsernameChange}
                placeholder="Enter your username"
                required
              />
            </div>
          )}
          <div className="form-group">
            <label className="text">Email</label>
            <input
              type="email"
              value={email}
              onChange={handleEmailChange}
              placeholder="Enter your email"
              required
            />
          </div>
          <div className="form-group">
            <label className="text">Password</label>
            <div className="password-input">
              <input
                type="password"
                value={password}
                onChange={handlePasswordChange}
                placeholder="Enter your password"
                required
              />
            </div>
          </div>
          {isSignUp && (
            <div className="form-group">
              <label className="text">Confirm Password</label>
              <input
                type="password"
                value={confirmedPassword}
                onChange={handleConfirmedPasswordChange}
                placeholder="Confirm your password"
                required
              />
              {!passwordMatch && (
                <p className="error-message">Passwords do not match</p>
              )}
            </div>
            )}
            <button
            type="submit"
            className="login-button"
            disabled={isSignUp && !passwordMatch} // Disable button if passwords don't match
            >
            {isSignUp ? 'Sign Up' : 'Login'}
            </button>
        </form>
        {!isSignUp ? (
          <p className="signup-link">
            Not a member?{' '}
            <a href="#" onClick={handleSignUpClick}>
              Sign up
            </a>
          </p>
        ) : (
          <p className="signup-link">
            Already have an account?{' '}
            <a href="#" onClick={handleSignInClick}>
              Sign in
            </a>
          </p>
        )}
      {showModal && (
        <div className="success-modal">
          <div className="success-modal-content">
            <span className="modal-close" onClick={handleModalClose }>
              &times;
            </span>
            <div className="success-image">
              <img src={success} alt="Success" />
            </div>
            <h3>Sign Up Successfully</h3>
          </div>
        </div>
      )}  
      </div>
    </div>
  );
}

export default LoginPage;