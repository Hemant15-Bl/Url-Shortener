import React, { useState, useEffect } from 'react'
import '../css/Home.css';
import { BarChart3, ExternalLink, Copy, Trash2, Loader2, Plus, LogOut, LinkIcon } from 'lucide-react';
import AnalyticsModal from './AnalyticsModal';
import { createShortenerLink, getAllLinkes, removeShortLink } from '../Services/Url-service';
import { axiosAPI } from '../Services/Auth';
import '../css/Dashboard.css'
import { toast } from "sonner";
import Swal from 'sweetalert2';

const DashBoard = () => {
  const [url, setUrl] = useState('');
  const [links, setLinks] = useState([]);
  const [selectedCode, setSelectedCode] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isShortening, setIsShortening] = useState(false);

  // --- NEW: Load links from DB on startup ---
  useEffect(() => {
    fetchLinks();
  }, []);

  const fetchLinks = async () => {
    try {
      setIsLoading(true);
      const resp = await getAllLinkes();
      // console.log("Raw data from DB:", resp);
      // Map the DB structure to the UI structure
      const formattedLinks = resp.map(item => ({
        original: item.originalUrl,
        short: `http://localhost:9096/${item.shortCode}`,
        code: item.shortCode
      }));

      setLinks(formattedLinks);


    } catch (err) {
      console.error("Error loading links:", err);
    } finally {
      setIsLoading(false);
    }
  };

  // -- Create: Short link ---------
  const handleShorten = async (e) => {
    e.preventDefault();
    setIsShortening(true);
    try {
      const data = await createShortenerLink(url);

      console.log("Raw data from DB:", data);
      // Add the new link to our local list
      const newLink = { original: url, short: data, code: data.split('/').pop() };
      setLinks([newLink, ...links]);
      console.log(links);

      setUrl('');
    } catch (err) {
      alert("Make sure URL-SERVICE is running on 9094!");
      console.error("Shorturl Not Created:- ",err);
    } finally {
      setIsShortening(false);
    }
  };

  // -- Copy: the link-url -----------------
  const copyToClipboard = (shortCode) => {
    const fullUrl = `${shortCode}`; // Your redirect URL
    navigator.clipboard.writeText(fullUrl).then(() => {
      toast.success('Link copied to clipboard');
    }).catch(err => {
      console.error('Failed to copy: ', err);
    });
  };

  // --Delete Short link from DB ------------
  const deleteLink = async (shortCode) => {
    Swal.fire({
    title: 'Remove Link?',
    text: "This action cannot be undone and all analytics will be lost.",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: "#ef4444", // Matching your red delete button
    cancelButtonColor: "#64748b",
    confirmButtonText: "Yes, Delete It!",
    background: '#ffffff',
    borderRadius: '12px'
  }).then(async (result) => { // Added 'async' here
    if (result.isConfirmed) {
      try {
        // 2. Perform the delete
        await removeShortLink(shortCode);

        // 3. Update local UI state 
        // Note: Ensure you use 'link.code' if that's what you mapped in useEffect
        setLinks(prev => prev.filter(link => link.code !== shortCode));

        // 4. Use a Toast for success (cleaner than a second popup)
        toast.success("Link deleted successfully");

      } catch (err) {
        console.error("Delete failed:", err);
        toast.error("Failed to delete link. Please try again.");
        // Optional: Show a specific error alert if it's a major failure
        // Swal.fire("Error!", "Could not reach the server.", 'error');
      }
    }
  });
  };

  const handleLogout = async () => {
    try {
      await axiosAPI.post('http://localhost:9096/api/v1/auth/logout', {}, { withCredentials: true });
      window.location.href = '/login';
    } catch (err) {
      console.error("Logout failed", err);
    }
  };
  return (
    <div className="dashboard-container">
      <nav className="dashboard-nav">
        <div className="nav-content">
          <div className="logo">
            <LinkIcon className="logo-icon" />
            <span>SwiftLink</span>
          </div>
          <button onClick={handleLogout} className="logout-btn">
            <LogOut size={18} /> Logout
          </button>
        </div>
      </nav>

      <main className="main-content">
        <div className="dashboard-header">
          <div className="header-text">
            <h1 className="text-xl">Link Overview</h1>
            <p className="text-sm">Manage and monitor your shortened URLs</p>
          </div>

          <div className="quick-stats">
            <div className="stat-item">
              <span className="stat-label">Total Links</span>
              <span className="stat-value">{links.length}</span>
            </div>
            <div className="stat-divider"></div>
            <div className="stat-item">
              <span className="stat-label">Total Clicks</span>
              <span className="stat-value">---</span>
            </div>
          </div>
        </div>

        {/* ACTION AREA: The Shortener Input */}
        <section className="input-section-compact">
          <form onSubmit={handleShorten} className="modern-form">
            <div className="input-box">
              <LinkIcon className="icon-muted" size={18} />
              <input
                type="url"
                placeholder="https://example.com/very-long-url-path"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                required
              />
              <kbd className="input-shortcut">CMD + K</kbd>
            </div>
            <button type="submit" disabled={isShortening} className="black-btn">
              {isShortening ? <Loader2 className="spinner" size={18} /> : "Shorten"}
            </button>
          </form>
        </section>

        {/* LIST AREA */}
        <div className="list-container">
          <div className="list-header-row">
            <span className="col-label">Link</span>
            <span className="col-label">Performance</span>
            <span className="col-label text-right">Actions</span>
          </div>

          <section className="links-section">
            {isLoading ? (
              <div className="loading-state">
                <Loader2 className="spinner" size={40} />
                <p>Loading your links...</p>
              </div>
            ) : links.length === 0 ? (
              <div className="empty-state">
                <p>No links yet. Start by shortening one!</p>
              </div>
            ) : (
              <div className="links-grid">
                {links.map((link, index) => (
                  <div key={index} className="premium-card grid-layout">
                    {/* Left: Info Section */}
                    <div className="card-main">
                      <div className="icon-badge">
                        <LinkIcon size={18} />
                      </div>
                      <div className="link-details">
                        <div className="link-header">
                          <a href={link.short} target="_blank" rel="noreferrer" className="short-url">
                            {link.short.replace('http://', '').replace('https://', '')}
                          </a>
                          <button onClick={() => copyToClipboard(link.short)} className="copy-icon-btn">
                            <Copy size={14} />
                          </button>
                        </div>
                        <p className="original-url-text">{link.original}</p>
                      </div>
                    </div>

                    {/* Right: Analytics & Actions */}
                    <div className="card-stats-actions">
                      <div className="stat-pill" onClick={() => setSelectedCode(link.code)}>
                        <BarChart3 size={14} />
                        <span>View Stats</span>
                      </div>
                    </div>

                    {/* Column 3: Actions */}
                    <div className="card-actions-wrapper">
                      <div className="action-group">
                        <button onClick={() => window.open(link.short)} className="ghost-btn" title="Open Link">
                          <ExternalLink size={18} />
                        </button>
                        <button onClick={() => deleteLink(link.code)} className="ghost-btn delete-hover" title="Delete">
                          <Trash2 size={18} />
                        </button>
                      </div>
                    </div>

                  </div>
                ))}
              </div>
            )}
          </section>

        </div>
      </main>

      {selectedCode && (
        <AnalyticsModal
          shortCode={selectedCode}
          onClose={() => setSelectedCode(null)}
        />
      )}
    </div>
  );
}

const actionButtonStyle = {
  background: '#f8fafc',
  border: '1px solid #e2e8f0',
  padding: '8px',
  borderRadius: '6px',
  cursor: 'pointer',
  color: '#64748b',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  transition: 'all 0.2s'
};

const deleteButtonStyle = {
  ...actionButtonStyle,
  color: '#ef4444', // Red text
  background: '#fef2f2', // Very light red background
  border: '1px solid #fee2e2',
};

export default DashBoard;