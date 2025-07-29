import { useState, useEffect } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { ToastProvider } from './components/Toast'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Expenses from './pages/Expenses'
import Budgets from './pages/Budgets'
import Profile from './pages/Profile'
import { useAuth } from './context/AuthContext'

export default function App() {
  const [darkMode, setDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode')
    return saved ? JSON.parse(saved) : false
  })
  const { user } = useAuth()

  useEffect(() => {
    document.documentElement.classList.toggle('dark', darkMode)
    localStorage.setItem('darkMode', JSON.stringify(darkMode))
  }, [darkMode])

  return (
    <ToastProvider>
      <Routes>
        <Route path="/" element={user ? <Navigate to="/dashboard" /> : <Landing darkMode={darkMode} setDarkMode={setDarkMode} />} />
        <Route path="/login" element={user ? <Navigate to="/dashboard" /> : <Login />} />
        <Route path="/register" element={user ? <Navigate to="/dashboard" /> : <Register />} />

        <Route path="/dashboard" element={
          <ProtectedRoute>
            <Layout darkMode={darkMode} setDarkMode={setDarkMode}><Dashboard /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/expenses" element={
          <ProtectedRoute>
            <Layout darkMode={darkMode} setDarkMode={setDarkMode}><Expenses /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/budgets" element={
          <ProtectedRoute>
            <Layout darkMode={darkMode} setDarkMode={setDarkMode}><Budgets /></Layout>
          </ProtectedRoute>
        } />
        <Route path="/profile" element={
          <ProtectedRoute>
            <Layout darkMode={darkMode} setDarkMode={setDarkMode}><Profile /></Layout>
          </ProtectedRoute>
        } />

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </ToastProvider>
  )
}
