import React, { useState } from 'react'
import { Box, Tabs, Tab } from '@mui/material'
import IngestForm from '../components/Transaction/IngestForm.jsx'
import HistoryTable from '../components/Transaction/HistoryTable.jsx'

const IngestPage = () => {
  const [activeTab, setActiveTab] = useState(0)

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue)
  }

  return (
    <Box>
      <Tabs value={activeTab} onChange={handleTabChange} sx={{ mb: 3 }}>
        <Tab label="Submit Transaction" />
        <Tab label="Transaction History" />
      </Tabs>

      {activeTab === 0 && <IngestForm />}
      {activeTab === 1 && <HistoryTable />}
    </Box>
  )
}

export default IngestPage 