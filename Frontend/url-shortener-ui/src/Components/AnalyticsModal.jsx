import React, { useEffect, useState } from 'react'
import { PieChart, Pie, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { X, ChevronDown } from 'lucide-react';
import { loadLinkHistory, loadLinkStats } from '../Services/Analytic-service';

const COLORS = ['#2563eb', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

const AnalyticsModal = ({ shortCode, onClose }) => {
  const [stats, setStats] = useState(null);
  const [history, setHistory] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  // 1. Initial Load: Get Summary and First Page of History
  useEffect(() => {

    const fetchInitialData = async () => {
      try {
        // Fetch Summary (Total clicks, etc.)
        const data = await loadLinkStats(shortCode);

        console.log("This data from 'LinkStats' Collection:",data);
        
        // Transform the summary for the Chart (Assuming backend still calculates this or use a dummy)
        const chartData = data.chartData || [];

        // Assign colors to the data items here
const coloredChartData = chartData.map((item, index) => ({
  ...item,
  fill: COLORS[index % COLORS.length] // Now COLORS is being used!
}));
        setStats({ ...data, chartData: coloredChartData});

        
        // Fetch First Page of History        
        fetchHistory(0);
      } catch (err) {
        console.error("Initial fetch error:", err);
      }
    };
    fetchInitialData();
  }, [shortCode]);

  // 2. Function to fetch paginated history
  const fetchHistory = async (pageToFetch) => {
    setLoading(true);
    try {
      const res = await loadLinkHistory(pageToFetch, shortCode);

              // console.log("histroy from 'Click_Log' Collection:",res);

      const newLogs = res.content;
      setHistory(prev =>{ return pageToFetch === 0 ? newLogs : [...prev, ...newLogs]});
      
      setHasMore(!res.last); // 'last' is provided by Spring Pageable
      setPage(pageToFetch + 1);
    } catch (err) {
      console.error("History fetch error:", err);
    } finally {
      setLoading(false);
    }
  };


  if (!stats) return null;

  
  return (
    <div style={modalOverlayStyle}>
      <div style={modalContentStyle}>
        <button onClick={onClose} style={closeButtonStyle}><X size={20} /></button>

        <h2>Insights for /{shortCode}</h2>
        <div style={{ display: 'flex', justifyContent: 'space-around', marginBottom: '20px' }}>
          <div style={statBox}>
            <small>Total Clicks</small>
            <h3>{stats.totalClick}</h3>
          </div>
          <div style={statBox}>
            <small>Top Browser</small>
            {/* Logic: Pick the first item from chartData or 'N/A' */}
            <h3>{stats.chartData?.[0]?.name || 'N/A'}</h3>
          </div>
        </div>

        {/* Chart Section */}
        <div style={{ width: '100%', height: '250px'}}>
          <h4>Browser Distribution</h4>
          {stats.chartData && stats.chartData?.length > 0 ? (
    <ResponsiveContainer width="100%" height="100%">
      <PieChart>
        <Pie
          data={stats.chartData}
          innerRadius={60}
          outerRadius={80}
          paddingAngle={5}
          dataKey="value"
          nameKey="name"
          label={({ name }) => name}
        />
        <Tooltip />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  ) : (
    <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
       <small>No browser data available yet...</small>
    </div>
  )}
        </div>

        {/* Paginated Table Section */}
        <div style={tableContainerStyle}>
          <h4>Recent Click Activity</h4>
          <table style={tableStyle}>
            <thead>
              <tr style={theadStyle}>
                <th>Timestamp</th>
                <th>IP</th>
                <th>Browser/OS</th>
              </tr>
            </thead>
            <tbody>
              {history.map((log, index) => (
                <tr key={index} style={rowStyle}>
                  <td>{new Date(log.clickedAt || log.timestamp).toLocaleString()}</td>
                  <td style={{ fontFamily: 'monospace' }}>{log.ipAddress}</td>
                  <td>{log.browser} / {log.os}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Load More Button */}
          {hasMore && (
            <button
              onClick={() => fetchHistory(page)}
              disabled={loading}
              style={loadMoreButtonStyle}
            >
              {loading ? 'Loading...' : <><ChevronDown size={16} /> Load More</>}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

// ... keep your existing modalOverlayStyle, modalContentStyle, etc. ...

const loadMoreButtonStyle = {
  width: '100%',
  padding: '10px',
  marginTop: '10px',
  background: '#f1f5f9',
  border: '1px dashed #cbd5e1',
  borderRadius: '6px',
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '5px',
  color: '#64748b'
};

const tableContainerStyle = { marginTop: '30px', maxHeight: '300px', overflowY: 'auto', borderTop: '1px solid #eee' };
const tableStyle = { width: '100%', borderCollapse: 'collapse', fontSize: '12px' };
const theadStyle = { position: 'sticky', top: 0, background: 'white' };
const rowStyle = { borderBottom: '1px solid #f1f5f9' };
const statBox = { textAlign: 'center', padding: '10px', background: '#f8fafc', borderRadius: '8px' };
const modalOverlayStyle = { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 };
const modalContentStyle = { background: 'white', padding: '30px', borderRadius: '12px', width: '90%', maxWidth: '600px', position: 'relative' };
const closeButtonStyle = { position: 'absolute', top: '15px', right: '15px', border: 'none', background: 'none', cursor: 'pointer' };

export default AnalyticsModal;