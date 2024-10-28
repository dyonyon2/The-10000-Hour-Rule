import React from 'react';

export default function LoadingSpinner() {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
      <div className="spinner" style={{ width: '50px', height: '50px', border: '5px solid white', borderRadius: '50%', borderTop: '5px solid #3498db', animation: 'spin 1s linear infinite' }}></div>

      <style jsx>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
}