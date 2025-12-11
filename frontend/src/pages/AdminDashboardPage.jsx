import { useState, useEffect } from 'react';
import adminService from '../services/adminService';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';

const AdminDashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [activity, setActivity] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [statsData, activityData] = await Promise.all([
        adminService.getDashboardStats(),
        adminService.getRecentActivity(10)
      ]);
      setStats(statsData);
      setActivity(activityData);
      setError(null);
    } catch (err) {
      console.error('Error loading dashboard data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(value || 0);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getSeverityColor = (severity) => {
    switch(severity) {
      case 'CRITICAL': return 'danger';
      case 'WARNING': return 'warning';
      default: return 'info';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600 mb-4">Error</h2>
          <p className="text-gray-600">{error}</p>
          <button
            onClick={loadDashboardData}
            className="mt-4 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Admin Dashboard
          </h1>
          <p className="text-gray-600 mt-2">
            Vue d'ensemble de la plateforme Purple Dog
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {/* Users */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">
                  Total Users
                </p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats?.totalUsers || 0}
                </p>
                <p className="text-sm text-green-600 mt-2">
                  +{stats?.newUsersThisMonth || 0} this month
                </p>
              </div>
              <div className="p-3 bg-blue-100 rounded-full">
                <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
            </div>
          </Card>

          {/* Products */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">
                  Total Products
                </p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats?.totalProducts || 0}
                </p>
                <p className="text-sm text-gray-600 mt-2">
                  {stats?.activeProducts || 0} active
                </p>
              </div>
              <div className="p-3 bg-green-100 rounded-full">
                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
            </div>
          </Card>

          {/* Orders */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">
                  Total Orders
                </p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stats?.totalOrders || 0}
                </p>
                <p className="text-sm text-gray-600 mt-2">
                  {stats?.pendingOrders || 0} pending
                </p>
              </div>
              <div className="p-3 bg-yellow-100 rounded-full">
                <svg className="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
            </div>
          </Card>

          {/* Revenue */}
          <Card className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">
                  Total Revenue
                </p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {formatCurrency(stats?.totalRevenue)}
                </p>
                <p className="text-sm text-green-600 mt-2">
                  {formatCurrency(stats?.revenueThisMonth)} this month
                </p>
              </div>
              <div className="p-3 bg-purple-100 rounded-full">
                <svg className="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
          </Card>
        </div>

        {/* Secondary Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Auctions</h3>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-gray-600">Total Auctions:</span>
                <span className="font-semibold">{stats?.totalAuctions || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Active:</span>
                <span className="font-semibold text-green-600">{stats?.activeAuctions || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Completed:</span>
                <span className="font-semibold">{stats?.completedAuctions || 0}</span>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Support Tickets</h3>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-gray-600">Total Tickets:</span>
                <span className="font-semibold">{stats?.totalTickets || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Open:</span>
                <span className="font-semibold text-yellow-600">{stats?.openTickets || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Resolved:</span>
                <span className="font-semibold text-green-600">{stats?.resolvedTickets || 0}</span>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Platform</h3>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-gray-600">Reviews:</span>
                <span className="font-semibold">{stats?.totalReviews || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Avg Rating:</span>
                <span className="font-semibold text-yellow-600">
                  {stats?.averageRating?.toFixed(1) || 0} ⭐
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Active Users:</span>
                <span className="font-semibold text-green-600">{stats?.activeUsers || 0}</span>
              </div>
            </div>
          </Card>
        </div>

        {/* Recent Activity */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-bold text-gray-900">
              Recent Activity
            </h2>
            <button
              onClick={loadDashboardData}
              className="text-purple-600 hover:text-purple-700 font-medium"
            >
              Refresh
            </button>
          </div>

          <div className="space-y-4">
            {activity.length === 0 ? (
              <p className="text-gray-500 text-center py-8">
                No recent activity
              </p>
            ) : (
              activity.map((item, index) => (
                <div
                  key={index}
                  className="flex items-start space-x-4 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                >
                  <Badge variant={getSeverityColor(item.severity)}>
                    {item.activityType}
                  </Badge>
                  <div className="flex-1">
                    <p className="text-gray-900">{item.description}</p>
                    {item.userEmail && (
                      <p className="text-sm text-gray-600 mt-1">
                        {item.userEmail}
                      </p>
                    )}
                    <p className="text-xs text-gray-500 mt-1">
                      {formatDate(item.timestamp)}
                    </p>
                  </div>
                </div>
              ))
            )}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
