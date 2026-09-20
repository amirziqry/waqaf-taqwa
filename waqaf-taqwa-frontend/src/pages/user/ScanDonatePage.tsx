import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { QrCode, ExternalLink, Loader2, AlertCircle } from "lucide-react";
import api from "../../api/client";
import { requestPayment } from "../../api/services/user/UserService";

export const ScanDonatePage: React.FC = () => {
  const navigate = useNavigate();
  const [amount, setAmount] = useState<string>("20");
  const [akadAgreed, setAkadAgreed] = useState(true);
  const [taxExempt, setTaxExempt] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const presetAmounts = ["10", "20", "50", "100", "200"];

  const handleInitiatePayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!akadAgreed || Number(amount) <= 0) return;

    setLoading(true);
    setErrorMsg("");

    try {
      // const response = await api.post('/personal/donations/payment-request', {
      //   amount: Number(amount),
      //   taxExempt: taxExempt,
      // });
      const response = await requestPayment(Number(amount));

      // const { paymentUrl, id } = response.data;
      const id = response.id;
      const paymentUrl = response.paymentUrl;

      if (paymentUrl) {
        // Save pending donation ID to check status upon return
        sessionStorage.setItem("pending_donation_id", id);

        // Redirect directly to Reliva's external portal
        window.location.href = paymentUrl;
      } else {
        throw new Error("Pautan pembayaran tidak sah diterima.");
      }
    } catch (err: any) {
      setErrorMsg(
        err.response?.data?.message ||
          "Gagal menyambung ke gerbang pembayaran Reliva. Sila pastikan anda telah log masuk.",
      );
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto space-y-6 pb-12">
      {/* Top Banner */}
      <div className="bg-white p-5 rounded-3xl border border-slate-100 shadow-xs flex items-center justify-between">
        <div>
          <h1 className="text-xl font-extrabold text-[#0F2028]">
            Waqaf Segera
          </h1>
          <p className="text-xs text-slate-400">
            Sumbangan pantas patuh syariah terus ke gerbang Reliva
          </p>
        </div>
        <div className="p-3 bg-emerald-50 text-[#1A8C4E] rounded-2xl">
          <QrCode className="w-6 h-6" />
        </div>
      </div>

      {errorMsg && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl flex items-center gap-2.5 text-rose-700 text-xs font-semibold">
          <AlertCircle className="w-4 h-4 shrink-0" />
          <span>{errorMsg}</span>
        </div>
      )}

      {/* Payment Form */}
      <form
        onSubmit={handleInitiatePayment}
        className="bg-white p-6 rounded-3xl border border-slate-100 shadow-xs space-y-5"
      >
        {/* Quick Preset Amounts */}
        <div className="space-y-2">
          <label className="text-xs font-extrabold text-[#0F2028]">
            Pilih Amaun (RM)
          </label>
          <div className="grid grid-cols-5 gap-2">
            {presetAmounts.map((val) => (
              <button
                type="button"
                key={val}
                onClick={() => setAmount(val)}
                className={`py-2.5 rounded-2xl text-xs font-extrabold transition ${
                  amount === val
                    ? "bg-[#1A8C4E] text-white shadow-xs"
                    : "bg-slate-50 text-slate-600 hover:bg-slate-100 border border-slate-100"
                }`}
              >
                RM {val}
              </button>
            ))}
          </div>
        </div>

        {/* Custom Amount Input */}
        <div className="space-y-1.5">
          <label className="text-xs font-extrabold text-[#0F2028]">
            Atau Masukkan Amaun Sendiri
          </label>
          <div className="h-12 bg-slate-50 border border-slate-200 rounded-2xl px-4 flex items-center gap-2 focus-within:bg-white focus-within:border-[#1A8C4E] transition">
            <span className="font-extrabold text-sm text-[#1A8C4E]">RM</span>
            <input
              type="number"
              min="1"
              required
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="w-full bg-transparent text-sm font-black outline-none text-slate-800"
            />
          </div>
        </div>

        {/* Akad & Tax Exemption */}
        <div className="p-4 bg-slate-50 rounded-2xl space-y-3 border border-slate-100">
          <label className="flex items-start gap-2.5 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={akadAgreed}
              onChange={(e) => setAkadAgreed(e.target.checked)}
              className="mt-0.5 rounded text-[#1A8C4E] focus:ring-[#1A8C4E]"
            />
            <span className="text-[11px] text-slate-600 font-medium leading-tight">
              <strong>Lafaz Akad:</strong> Saya berniat mewakafkan dana sebanyak{" "}
              <strong>RM {amount}</strong> ini kerana Allah Taala untuk
              kemaslahatan ummah.
            </span>
          </label>

          {/* <label className="flex items-start gap-2.5 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={taxExempt}
              onChange={(e) => setTaxExempt(e.target.checked)}
              className="mt-0.5 rounded text-[#1A8C4E] focus:ring-[#1A8C4E]"
            />
            <span className="text-[11px] text-slate-600 font-medium leading-tight">
              Mohon pelepasan resit cukai LHDN rasmi.
            </span>
          </label> */}
        </div>

        {/* Action Button */}
        <button
          type="submit"
          disabled={loading || !akadAgreed || Number(amount) <= 0}
          className="w-full h-12 bg-[#1A8C4E] hover:bg-[#15703E] disabled:bg-slate-300 text-white font-bold rounded-2xl text-xs flex items-center justify-center gap-2 shadow-[0_4px_12px_rgba(26,140,78,0.25)] transition active:scale-[0.99]"
        >
          {loading ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              <span>Menghubungkan ke Gerbang Reliva...</span>
            </>
          ) : (
            <>
              <span>
                Teruskan ke Gerbang Pembayaran Reliva (RM{" "}
                {Number(amount).toFixed(2)})
              </span>
              <ExternalLink className="w-4 h-4" />
            </>
          )}
        </button>
      </form>
    </div>
  );
};
