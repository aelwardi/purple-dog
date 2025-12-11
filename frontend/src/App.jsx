import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './contexts/AuthContext';
import ErrorBoundary from './components/errors/ErrorBoundary';
import ErrorPage from './components/errors/ErrorPage';
import ProtectedRoute from './components/auth/ProtectedRoute';
import MainLayout from './layouts/MainLayout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import SearchPage from './pages/SearchPage';
import CategoryPage from './pages/CategoryPage';
import DashboardPage from './pages/DashboardPage';
import PricingPage from './pages/PricingPage';
import FeedbackPage from './pages/FeedbackPage';
import UnifiedSupportPage from './pages/UnifiedSupportPage';

function App() {
  return (
    <BrowserRouter>
      <ErrorBoundary>
        <AuthProvider>
          <Toaster position="top-right" />
          <Routes>
            <Route element={<MainLayout />}>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/forgot-password" element={<ForgotPasswordPage />} />
              <Route path="/reset-password" element={<ResetPasswordPage />} />
              <Route path="/search" element={<SearchPage />} />
              <Route path="/category/:categoryId" element={<CategoryPage />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/contact" element={<ContactPage />} />
              <Route path="/pricing" element={<PricingPage />} />
            </Route>
            {/* Dashboard and Feedback routes without MainLayout - Protected */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <DashboardPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/feedback"
              element={
                <ProtectedRoute>
                  <FeedbackPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={<Navigate to="/dashboard" replace />}
            />
            {/* Support */}
            <Route
              path="/support"
              element={
                <ProtectedRoute>
                  <UnifiedSupportPage />
                </ProtectedRoute>
              }
            />
            {/* 404 Page */}
            <Route path="*" element={<ErrorPage code={404} />} />
          </Routes>
        </AuthProvider>
      </ErrorBoundary>
    </BrowserRouter>
  );
}

export default App;
